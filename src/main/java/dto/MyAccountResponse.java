package dto;

import entities.User;
import entities.Address;
import entities.UserPreferences;

public record MyAccountResponse(String firstName, String lastName,
                                String email, String phoneNumber, String departmentName,
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
                user.getPhoneNumber(),
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

        StringBuilder formatted = new StringBuilder();
        if (address.getLocality() != null) {
            if (address.getLocality().getCounty() != null) {
                formatted.append(address.getLocality().getCounty().getName()).append(", ");
            }
            formatted.append(address.getLocality().getName()).append(", ");
        }
        formatted.append(address.getStreet()).append(" ").append(address.getNumber());
        if (address.getApartmentBlock() != null && !address.getApartmentBlock().isBlank()) {
            formatted.append(", Bloc ").append(address.getApartmentBlock());
        }
        if (address.getFloor() != null) {
            formatted.append(", Etaj ").append(address.getFloor());
        }
        if (address.getPostalCode() != null && !address.getPostalCode().isBlank()) {
            formatted.append(" ").append(address.getPostalCode());
        }
        return formatted.toString();
    }
}

