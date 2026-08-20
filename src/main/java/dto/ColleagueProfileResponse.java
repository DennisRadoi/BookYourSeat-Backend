package dto;

import entities.User;
import entities.UserPreferences;

import java.util.List;

public record ColleagueProfileResponse(
        String fullname,
        String departmentName,
        String role,
        boolean quietPlace, boolean nearWindow,
        String preferedColleague,
        String profilePhoto,
        String preferredStartTime,
        String daysOfWeek,
        String location,
        boolean isFavorite,
        List<BookingDTOV2> bookingDto
) {
    public static ColleagueProfileResponse fromEntity(User user, String preferedColleague, boolean isFavorite,
                      List<BookingDTOV2> list, String location) {
        UserPreferences preferinte = user.getUserPreferences();
        return new ColleagueProfileResponse(
                user.getFirstName() + " " + user.getLastName(),
                user.getDepartment() == null ? null : user.getDepartment().getName(),
                user.getRole(),
                // Răspunsul expune booleans primitive. Pentru colegii care nu
                // au încă preferințe, `null` ar fi despachetat automat și ar
                // produce un NullPointerException (profilul nu se mai încărca).
                preferinte != null && Boolean.TRUE.equals(preferinte.getQuietPlace()),
                preferinte != null && Boolean.TRUE.equals(preferinte.getNearWindow()),
                preferedColleague,
                user.getProfilePhoto(),
                preferinte == null ? null : String.valueOf(preferinte.getPreferredStartTime()),
                preferinte == null ? null : preferinte.getDaysOfWeek(),
                location,
                isFavorite,
                list
        );
    }
}
