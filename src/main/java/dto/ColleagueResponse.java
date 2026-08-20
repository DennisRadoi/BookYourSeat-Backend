package dto;

import entities.User;

public record ColleagueResponse(Integer id, String fullname, String role,
                                String status, String location, String building, String room, boolean isFavorite) {

    public static ColleagueResponse fromEntity(User user, String status, String location, String building,
                                               boolean isFavorite) {
        return fromEntity(user, status, location, building, null, isFavorite);
    }

    public static ColleagueResponse fromEntity(User user, String status, String location, String building,
                                               String room, boolean isFavorite) {
        return new ColleagueResponse(
                user.getId(),
                user.getFirstName() + " " + user.getLastName(),
                user.getRole(),
                status,
                location,
                building,
                room,
                isFavorite
        );
    }
}
