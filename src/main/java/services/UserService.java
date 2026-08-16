package services;

import dto.*;
import entities.*;
import entities.enums.AddressType;
import entities.enums.BookingStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repositories.*;
import utils.Filter;
import utils.Utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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

    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilizatorul nu exista."));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilizator cu acest email" + email + " nu exista"));
    }

    public List<User> findByLastName(String lastName) {
        return userRepository.findByLastName(lastName);
    }

    public List<User> findByRole(String role) {
        return userRepository.findByRole(role);
    }

    public User findByLastNameAndFirstName(String lastName, String firstName) {
        return userRepository.findByLastNameAndFirstName(lastName, firstName)
                .orElseThrow(() -> new RuntimeException("Utilizatorul nu exista."));
    }

    public User findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Utilizatorul nu exista."));
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

        String favorit = favoriti.isEmpty() ? null : Utils.getRandomFavoriteColleage(favoriti);

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

        User currentUser = findById(userId);
        if (!colleague.getIsActive()) {
            return ColleagueResponse.fromEntity(colleague, "inactiv",
                    null, isFavorite);
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
                    colleague, "remote", null, isFavorite
            );
        }
        else if (activeBooking.getSeat() == null) {
            return ColleagueResponse.fromEntity(
                    colleague, "la birou", String.valueOf(activeBooking.getRoom().getFloor()), isFavorite
            );
        } else {
            return ColleagueResponse.fromEntity(
                    colleague, "la birou", String.valueOf(activeBooking.getSeat().getRoom().getFloor()), isFavorite
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
                .isEmpty() ? null : Utils.getRandomFavoriteColleage(listOfFavoritesOfColleague);

        LocalDate threeWeeksAgo = LocalDate.now().minusWeeks(3);
        List<BookingDTOV2> bookings = bookingRepository
                .findByUserIdAndEndDateGreaterThanEqual(colleague.getId(), threeWeeksAgo)
                .stream()
                .map(b -> BookingDTOV2.fromEntity(b))
                .toList();

        String location = null;
        if (!colleague.getIsActive()) {
            location = new String("inactiv");
        }

        Booking activeBooking = bookingRepository.findByUserIdAndStatus(
                        colleague.getId(), BookingStatus.CONFIRMATA
                ).stream()
                .filter(b -> Utils.isActiveBookingNow(
                        LocalDate.now(), LocalTime.now(), b))
                .findFirst()
                .orElse(null);

        if (activeBooking == null) {
            location = new String("remote");
        }
        else if (activeBooking.getSeat() == null) {
            location = String.valueOf(activeBooking.getRoom().getFloor());
        } else {
            location = String.valueOf(activeBooking.getSeat().getRoom().getFloor());
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
        updateAdress(user, updateMyAdressRequest);

        if (request.fullname() != null) {
            String[] name = request.fullname().split(" ");
            user.setFirstName(name[0]);
            user.setLastName(name[1]);
        }
        if (request.phoneNumber() != null) {
            if (request.phoneNumber().length() != 10) {
                throw new RuntimeException("Phone number size is invalid.");
            }
            user.setPhoneNumber(request.phoneNumber());
        }
        if (request.email() != null) {
            if (userRepository.existsByEmailAndIdNot(request.email(), currentUserId)) {
                throw new RuntimeException("Email address already in use.");
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
        userRepository.save(user);

        List<User> list = favoriteColleagueRepository.findFavoriteUsersByUserId(user.getId());
        String preferredColleague = list.isEmpty()
                ? null
                : Utils.getRandomFavoriteColleage(list);

        return MyAccountResponse.fromEntity(
                user,
                preferredColleague
        );
    }

    private void updateAdress(User u, UpdateMyAdressRequest request) {
        if (request == null) {
            return;
        }
        Address adress = u.getAddress();
        Address newAddress = new Address();

        if (request.street() != null) {
            newAddress.setStreet(request.street());
        } else {
            newAddress.setStreet(adress.getStreet());
        }
        if (request.number() != null) {
            newAddress.setNumber(request.number());
        } else {
            newAddress.setNumber(adress.getNumber());
        }
        if (request.apartmentBlock() != null) {
            newAddress.setApartmentBlock(request.apartmentBlock());
        } else {
            newAddress.setApartmentBlock(adress.getApartmentBlock());
        }
        if (request.postalCode() != null) {
            newAddress.setPostalCode(request.postalCode());
        } else {
            newAddress.setPostalCode(adress.getPostalCode());
        }
        if (request.floor() != null) {
            newAddress.setFloor(request.floor());
        } else {
            newAddress.setFloor(adress.getFloor());
        }
        if (request.county() != null && request.locality() == null) {
            throw new RuntimeException("If you u want to modify the county, you have to provide the locality too");
        }
        Locality locality = adress.getLocality();
        if (request.locality() != null) {
            County county;
            if (request.county() != null) {
                county = countyRepository.findByName(request.county()).orElse(null);
                if (county == null) {
                    county = new County();
                    county.setName(request.county());
                    countyRepository.save(county);
                }
            }
            else {
                county = adress.getLocality().getCounty();
            }
            locality = localityRepository.findByNameAndCountyName(request.locality(),
                            county.getName())
                    .orElse(null);
            if (locality == null) {
                locality = new Locality();
                locality.setName(request.locality());
                locality.setCounty(county);
                localityRepository.save(locality);
            }
        }
        newAddress.setLocality(locality);
        newAddress.setType(AddressType.DE_DOMICILIU);
        addressRepository.save(newAddress);
        u.setAddress(newAddress);
    }

    @Transactional // transactional face update-ul in SQL chiar daca am modificat doar obiectul in Java prin dirty checking
    public MyAccountResponse updateAccountPagePreferences(Integer currentUserId,
                                             UpdateAccountPreferencesRequest request) {
        if (request == null) {
            throw new RuntimeException("Request is null.");
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
                : Utils.getRandomFavoriteColleage(list);
        return MyAccountResponse.fromEntity(u, preferredColleague);
    }
}
