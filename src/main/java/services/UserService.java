package services;

import controllers.InvitationController;
import dto.*;
import entities.*;
import entities.enums.BookingStatus;
import entities.enums.InvitationStatus;
import exceptions.EmailAlreadyExistsException;
import exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repositories.*;
import utils.Filter;
import utils.Utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FavoriteColleagueRepository favoriteColleagueRepository;
    private final BookingRepository bookingRepository;
    private final DepartmentRepository departmentRepository;
    private final AddressRepository addressRepository;
    private final CountyRepository countyRepository;
    private final LocalityRepository localityRepository;
    private final BuildingRepository buildingRepository;
    private final OfficeInvitationRepository officeInvitationRepository;
    private final NotificationRepository notificationRepostiory;
    private final UserNotificationRepository userNotificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + email, -1));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + email, -1));
    }

    public List<User> findByLastName(String lastName) {
        return userRepository.findByLastName(lastName);
    }

    public List<User> findByRole(String role) {
        return userRepository.findByRole(role);
    }

    public User findByLastNameAndFirstName(String lastName, String firstName) {
        return userRepository.findByLastNameAndFirstName(lastName, firstName)
                .orElseThrow(() -> new ResourceNotFoundException("User", -1));
    }

    public User findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User with phone " + phoneNumber, -1));
    }

    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public List<User> findAllByIsActiveTrue() {
        return userRepository.findAllByIsActiveTrue();
    }

    public List<User> findAllByIsActiveFalse() {
        return userRepository.findAllByIsActiveFalse();
    }

    public List<User> findAllByDepartmentName(String name) {
        return userRepository.findALlByDepartmentName(name);
    }

    public MyAccountResponse getMyAccountResponse(Integer userId) {
        User user = findById(userId);

        List<User> favoriti = favoriteColleagueRepository.findFavoriteUsersByUserId(userId);

        String favorit = favoriti.isEmpty() ? null : Utils.getRandomFavoriteColleague(favoriti);

        return MyAccountResponse.fromEntity(user, favorit);
    }

//    public List<ColleagueResponse> getColleagues(Integer userId) {
//        return userRepository.findAll()
//                .stream()
//                .filter(u -> !u.getId().equals(userId))
//                .map(u -> toColleagueResponse(userId, u))
//                .toList();
//    }

    public PageResponse<ColleagueResponse> getColleagues(
            Integer currentUserId,
              String search,
              String status,
              Integer floor,
              String building,
              Boolean favorite,
            int page,
            int size) {
        List<ColleagueResponse> filtered = userRepository.findAll()
                .stream()
                .filter(u -> !u.getId().equals(currentUserId))
                .map(u -> toColleagueResponse(currentUserId, u))
                .filter(response -> Filter.matchesSearch(response, search))
                .filter(response -> Filter.matchesStatus(response, status))
                  .filter(response -> Filter.matchesFloor(response, floor))
                  .filter(response -> Filter.matchesBuilding(response, building))
                .filter(response -> Filter.matchesFavorite(response, favorite))
                .toList();

        int fromIndex = page * size;

        List<ColleagueResponse> content = fromIndex >= filtered.size()
                ? List.of()
                : filtered.subList(
                fromIndex,
                Math.min(fromIndex + size, filtered.size())
        );

        int totalPages = (int) Math.ceil(
                (double) filtered.size() / size
        );

        return new PageResponse<>(
                content,
                page,
                size,
                filtered.size(),
                totalPages
        );
    }

    public ColleagueResponse toColleagueResponse(Integer userId, User colleague) {
        List<User> listOfFavorites = favoriteColleagueRepository.findFavoriteUsersByUserId(userId);
        boolean isFavorite = listOfFavorites.contains(colleague);

        // Check colleague's active status (not currentUser's)
        if (!colleague.getIsActive()) {
            return ColleagueResponse.fromEntity(colleague, "inactiv",
                    null, null, isFavorite);
        }

        Booking activeBooking = bookingRepository.findByUserIdAndStatus(
                        colleague.getId(), BookingStatus.CONFIRMATA
                ).stream()
                .filter(b -> Utils.isActiveBookingNow(
                        LocalDate.now(), LocalTime.now(), b))
                .findFirst()
                .orElse(null);

        if (activeBooking == null) {
              return ColleagueResponse.fromEntity(
                      colleague, "remote", null, null, isFavorite
              );
          }
          else if (activeBooking.getSeat() == null) {
              return ColleagueResponse.fromEntity(
                      colleague, "la birou", String.valueOf(activeBooking.getRoom().getFloor()),
                      activeBooking.getRoom().getBuilding().getName(), activeBooking.getRoom().getName(), isFavorite
              );
          } else {
              return ColleagueResponse.fromEntity(
                      colleague, "la birou", String.valueOf(activeBooking.getSeat().getRoom().getFloor()),
                      activeBooking.getSeat().getRoom().getBuilding().getName(), activeBooking.getSeat().getRoom().getName(), isFavorite
              );
        }
    }

    public ColleagueProfileResponse toColleagueProfileResponse(Integer colleagueId, Integer userId) {
        User colleague =  findById(colleagueId);
        List<User> listOfFavorites = favoriteColleagueRepository.findFavoriteUsersByUserId(userId);
        boolean isFavorite = listOfFavorites.contains(colleague);

        List<User> listOfFavoritesOfColleague = favoriteColleagueRepository
                .findFavoriteUsersByUserId(colleague.getId());
        String favorit = listOfFavoritesOfColleague
                .isEmpty() ? null : Utils.getRandomFavoriteColleague(listOfFavoritesOfColleague);

        LocalDate threeWeeksAgo = LocalDate.now().minusWeeks(3);
        List<BookingDTOV2> bookings = bookingRepository
                .findByUserIdAndEndDateGreaterThanEqual(colleague.getId(), threeWeeksAgo)
                .stream()
                .map(b -> BookingDTOV2.fromEntity(b))
                .toList();

        String location;
        if (!colleague.getIsActive()) {
            // Return early — no need to check bookings for inactive users
            return ColleagueProfileResponse.fromEntity(
                    colleague, favorit, isFavorite, bookings, "inactiv"
            );
        }

        Booking activeBooking = bookingRepository.findByUserIdAndStatus(
                        colleague.getId(), BookingStatus.CONFIRMATA
                ).stream()
                .filter(b -> Utils.isActiveBookingNow(
                        LocalDate.now(), LocalTime.now(), b))
                .findFirst()
                .orElse(null);

        if (activeBooking == null) {
            location = "remote";
        } else {
            Room room = activeBooking.getSeat() == null ? activeBooking.getRoom() : activeBooking.getSeat().getRoom();
            location = room.getBuilding().getName() + " · Etaj " + room.getFloor() + " · " + room.getName();
        }

        return ColleagueProfileResponse.fromEntity(
                colleague,
                favorit,
                isFavorite,
                bookings,
                location
        );
    }

    public MySettingsResponse toMySettingsResponse(Integer userId) {
        User currentUser = findById(userId);
        return MySettingsResponse.fromEntity(currentUser);
    }

    @Transactional
    public MyAccountResponse updateProfile(Integer currentUserId,
                                           UpdateMyAccountRequest request) {
        User user = findById(currentUserId);

        UpdateMyAdressRequest updateMyAdressRequest = request.updateMyAdressRequest();
        updateAddress(user, updateMyAdressRequest);

        if (request.fullname() != null) {
            String[] name = request.fullname().split(" ", 2);
            if (name.length < 2) {
                throw new IllegalArgumentException("Fullname must contain first and last name separated by space.");
            }
            user.setFirstName(name[0]);
            user.setLastName(name[1]);
        }
        if (request.phoneNumber() != null) {
            if (request.phoneNumber().length() != 10) {
                throw new IllegalArgumentException("Phone number size is invalid.");
            }
            user.setPhoneNumber(request.phoneNumber());
        }
        if (request.email() != null) {
            if (userRepository.existsByEmailAndIdNot(request.email(), currentUserId)) {
                throw new EmailAlreadyExistsException(request.email());
            }
            user.setEmail(request.email());
        }
        if (request.profilePhoto() != null) {
            user.setProfilePhoto(request.profilePhoto());
        }

          if (request.departmentName() != null) {
            Department department = departmentRepository
                    .findByName(request.departmentName())
                    .orElseThrow(() -> new RuntimeException(
                            "Departamentul nu exista."
                    ));
              user.setDepartment(department);
          }
          if (request.role() != null) {
              user.setRole(request.role());
          }
          if (request.employmentDate() != null) {
              user.setEmploymentDate(LocalDate.parse(request.employmentDate()));
          }
        userRepository.save(user);

        List<User> list = favoriteColleagueRepository.findFavoriteUsersByUserId(user.getId());
        String preferredColleague = list.isEmpty()
                ? null
                : Utils.getRandomFavoriteColleague(list);

        return MyAccountResponse.fromEntity(
                user,
                preferredColleague
        );
    }

    private void updateAddress(User u, UpdateMyAdressRequest request) {
        if (request == null) {
            return;
        }
        Address address = u.getAddress();

        if (request.street() != null) {
            address.setStreet(request.street());
        }
        if (request.number() != null) {
            address.setNumber(request.number());
        }
        if (request.apartmentBlock() != null) {
            address.setApartmentBlock(request.apartmentBlock());
        }
        if (request.postalCode() != null) {
            address.setPostalCode(request.postalCode());
        }
        if (request.floor() != null) {
            address.setFloor(request.floor());
        }
        if (request.county() != null && request.locality() == null) {
            throw new IllegalArgumentException("If you want to modify the county, you have to provide the locality too");
        }
        if (request.locality() != null) {
            County county;
            if (request.county() != null) {
                county = countyRepository.findByName(request.county()).orElse(null);
                if (county == null) {
                    county = new County();
                    county.setName(request.county());
                    countyRepository.save(county);
                }
            } else {
                county = address.getLocality().getCounty();
            }
            Locality locality = localityRepository.findByNameAndCountyName(request.locality(),
                            county.getName())
                    .orElse(null);
            if (locality == null) {
                locality = new Locality();
                locality.setName(request.locality());
                locality.setCounty(county);
                localityRepository.save(locality);
            }
            address.setLocality(locality);
        }
    }

    @Transactional
    public MyAccountResponse updateAccountPagePreferences(Integer currentUserId,
                                             UpdateAccountPreferencesRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request is null.");
        }
        User u = findById(currentUserId);
        UserPreferences userPreferences = u.getUserPreferences();

        if (request.nearWindow() != null) {
            userPreferences.setNearWindow(request.nearWindow());
        }
        if (request.quietPlaces() != null) {
            userPreferences.setQuietPlace(request.quietPlaces());
        }
        List<User> list = favoriteColleagueRepository.findFavoriteUsersByUserId(u.getId());
        String preferredColleague = list.isEmpty()
                ? null
                : Utils.getRandomFavoriteColleague(list);
        return MyAccountResponse.fromEntity(u, preferredColleague);
    }

    @Transactional
    public MySettingsResponse updateSettings(Integer currentUserId,
                                             UpdateSettingsPreferencesRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request is null.");
        }
        User u = findById(currentUserId);
        UserPreferences userPreferences = u.getUserPreferences();
        if (request.reminderBeforeBooking() != null) {
            userPreferences.setReminderBeforeBooking(request.reminderBeforeBooking());
        }
        if (request.receivesNotificationOnEmail() != null) {
            userPreferences.setBookingConfirmationOnEmail(request.receivesNotificationOnEmail());
        }
        if (request.daysOfWeek() != null) {
            List<String> parts = new ArrayList<>();
            if (request.daysOfWeek().contains("MONDAY"))    parts.add("1");
            if (request.daysOfWeek().contains("TUESDAY"))   parts.add("2");
            if (request.daysOfWeek().contains("WEDNESDAY")) parts.add("3");
            if (request.daysOfWeek().contains("THURSDAY"))  parts.add("4");
            if (request.daysOfWeek().contains("FRIDAY"))    parts.add("5");
            userPreferences.setDaysOfWeek(String.join(",", parts));
        }
        if (request.preferredEndTime() != null) {
            userPreferences.setPreferredEndTime(LocalTime.parse(request.preferredEndTime()));
        }
        if (request.preferredStartTime() != null) {
            userPreferences.setPreferredStartTime(LocalTime.parse(request.preferredStartTime()));
        }
        if (request.preferredBuilding() != null) {
            if (buildingRepository.existsByName(request.preferredBuilding())) {
                userPreferences.setPreferredBuilding(
                        buildingRepository.findByName(request.preferredBuilding()).orElse(null));
            } else {
                throw new IllegalArgumentException("The building " + request.preferredBuilding() + " doesn't exist.");
            }
        }
        return MySettingsResponse.fromEntity(u);
    }

    public List<ColleagueResponse> getListOfFavorites(Integer currentUserId) {
        List<User> favorites = favoriteColleagueRepository.findFavoriteUsersByUserId(currentUserId);
        return favorites.stream()
                .map(colleague -> toColleagueResponse(currentUserId, colleague))
                .toList();
    }

    @Transactional
    public InvitationResponse createInvitation(Integer currentUserId,
                                               CreateInvitationRequest request, Integer addresseId) {
        User u = findById(currentUserId);
        User addresse = findById(addresseId);

        if (u.getId().equals(addresse.getId())) {
            throw new RuntimeException("Can't send an invitation to yourself.");
        }

        OfficeInvitation officeInvitation = new OfficeInvitation();
        officeInvitation.setUser(u);
        officeInvitation.setAddressee(addresse);
        officeInvitation.setMessage(request.message());
        officeInvitation.setProposedDate(request.proposedDate());
        officeInvitationRepository.save(officeInvitation);

        Notification notification = new Notification();
        notification.setUser(u);
        notification.setType("invitatie");
        notification.setOfficeInvitation(officeInvitation);
        String message = "";
        notification.setMessage(
                u.getFirstName() + " " + u.getLastName() + " te-a invitat la birou pe " + request.proposedDate() + ".");
        notificationRepostiory.save(notification);

        UserNotification userNotification = new UserNotification();
        userNotification.setUser(addresse);
        userNotification.setNotification(notification);
        userNotificationRepository.save(userNotification);

        Map<String, Object> vars = new HashMap<>();
        vars.put("senderName", u.getFirstName() + " " + u.getLastName());
        vars.put("addresseeName", addresse.getFirstName());
        vars.put("proposedDate", request.proposedDate().toString());
        vars.put("message", request.message());

        emailService.sendEmail(
                addresse.getEmail(),
                "Invitație la birou de la " + u.getFirstName(),
                "office-invitation",
                vars
        );

        return InvitationResponse.fromEntity(officeInvitation);
    }

    @Transactional // transactional face update-ul in SQL chiar daca am modificat doar obiectul in Java prin dirty checking
    public InvitationResponse updateInvitationStatus(AnswerInvitationRequest request,
                                                     Integer currentUserId,
                                                     Integer invitationId) {
        OfficeInvitation invitation = officeInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation", invitationId));
        User addressee = invitation.getAddressee();
        User sender = invitation.getUser();

        if (invitation.getStatus() != InvitationStatus.IN_ASTEPTARE
                || invitation.getAnsweredAt() != null) {
            throw new IllegalStateException(
                    "Invitation has already been answered."
            );
        }

        if (request.invitationStatus() != InvitationStatus.ACCEPTATA
                && request.invitationStatus() != InvitationStatus.REFUZATA) {
            throw new IllegalArgumentException(
                    "You can only accept or refuse the invitation."
            );
        }

            invitation.setStatus(request.invitationStatus());
            invitation.setAnsweredAt(OffsetDateTime.now());
            String responseText = request.invitationStatus() == InvitationStatus.ACCEPTATA ? "a acceptat invitatia ta."
                    : "a refuzat invitatia ta.";

            Notification notification = new Notification();
            notification.setUser(addressee);
            notification.setType("invite_response");
            notification.setMessage(
                    addressee.getFirstName() + " " + addressee.getLastName()
                            + " " + responseText
            );
            notification.setOfficeInvitation(invitation);
            notificationRepostiory.save(notification);

            UserNotification userNotification = new UserNotification();
            userNotification.setUser(sender);
            userNotification.setNotification(notification);
            userNotificationRepository.save(userNotification);

            return InvitationResponse.fromEntity(invitation);
    }


    public List<InvitationResponse> getInvitations(Integer currentUserId, String direction) {
        return officeInvitationRepository
                .findAllByUserIdOrAddresseeIdOrderByCreatedAtDesc(currentUserId, currentUserId)
                .stream()
                .filter(invitation -> switch (direction.toLowerCase(Locale.ROOT)) {
                    case "all" -> true;
                    case "sent" -> invitation.getUser().getId().equals(currentUserId);
                    case "received" -> invitation.getAddressee().getId().equals(currentUserId);
                    default -> throw new IllegalArgumentException("Direction must be all, sent, or received.");
                })
                .map(InvitationResponse::fromEntity)
                .toList();
    }

    public void changePassword(ChangePasswordRequest request, Integer currentUserId) {
        User u = findById(currentUserId);

        if (!passwordEncoder.matches(
                request.currentPassword(),
                u.getPasswordHash()
        )) {
            throw new BadCredentialsException("The password you typed does not match with your current password.");
        }

        if (passwordEncoder.matches(
                request.newPassword(),
                u.getPasswordHash()
        )) {
            throw new BadCredentialsException(
                    "New password must be different from current password."
            );
        }

        u.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(u);
    }
}
