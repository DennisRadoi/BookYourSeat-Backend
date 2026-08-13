package services;

import dto.ColleagueResponse;
import dto.MyAccountResponse;
import entities.Address;
import entities.Booking;
import entities.FavoriteColleague;
import entities.User;
import entities.enums.BookingStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repositories.BookingRepository;
import repositories.FavoriteColleagueRepository;
import repositories.UserRepository;
import utils.Utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;

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

    public ColleagueResponse toColleagueResponse(Integer userId, User colleague) {
        List<User> listOfFavorites = favoriteColleagueRepository.findFavoriteUsersByUserId(userId);
        boolean isFavorite = listOfFavorites.contains(colleague);

        User currentUser = findById(userId);
        if (!currentUser.getIsActive()) {
            return ColleagueResponse.fromEntity(currentUser, "inactiv",
                    null, isFavorite);
        }

        Booking activeBooking = bookingRepository.findByUserIdAndStatus(
                userId, BookingStatus.confirmata
        ).stream()
                .filter(b -> Utils.isActiveBookingNow(
                LocalDate.now(), LocalTime.now(), b))
                .findFirst()
                .orElse(null);

        if (activeBooking == null) {
            return ColleagueResponse.fromEntity(
                    currentUser, "remote", "remote", isFavorite
            );
        }

        if (activeBooking.getSeat() == null) {
            return ColleagueResponse.fromEntity(
                    currentUser, "la birou", String.valueOf(activeBooking.getRoom().getFloor()), isFavorite
            );
        }
        else {
            return ColleagueResponse.fromEntity(
                    currentUser, "la birou", String.valueOf(activeBooking.getSeat().getRoom().getFloor()), isFavorite
            );
        }
    }
}