package services;

import dto.*;
import entities.Booking;
import entities.User;
import entities.enums.BookingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repositories.BookingRepository;
import repositories.FavoriteColleagueRepository;
import repositories.UserRepository;
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
        if (!currentUser.getIsActive()) {
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

        if (activeBooking.getSeat() == null) {
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

        if (activeBooking.getSeat() == null) {
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
}
