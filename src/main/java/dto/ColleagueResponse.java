package dto;

import entities.User;

public record ColleagueResponse(String fullname, String role,
                                String status, String location, boolean isFavorite) {

    public static ColleagueResponse fromEntity(User user, String status, String location,
                                               boolean isFavorite) {
        return new ColleagueResponse(
                user.getFirstName() + " " + user.getLastName(),
                user.getRole(),
                status,
                location,
                isFavorite
        );
    }
}
