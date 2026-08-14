package dto;

import entities.User;
import entities.Address;
import entities.UserPreferences;

public record MyAccountResponse(String firstName, String lastName,
                                String email, String departmentName,
                                String formatedAdress, String profilePhoto,
                                boolean quietPlace, boolean nearWindow,
                                String preferedColleague)
{
    public static MyAccountResponse fromEntity(User user, String preferedColleague) {
        UserPreferences preferinte = user.getUserPreferences();
        return new MyAccountResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getDepartment() == null ? null : user.getDepartment().getName(),
                formatAddress(user.getAddress()),
                user.getProfilePhoto(),
                preferinte == null ? null : preferinte.getQuietPlace(),
                preferinte == null ? null : preferinte.getNearWindow(),
                preferedColleague
            );
    }

    private static String formatAddress(Address address) {
        if (address == null) {
            return null;
        }

        return address.getLocality().getCounty().getName()
                + ", " + address.getLocality().getName()
                + ", " + address.getStreet()
                + " " + address.getNumber();
    }
}

