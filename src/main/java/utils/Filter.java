package utils;

import dto.ColleagueResponse;

public class Filter {
    public static boolean matchesSearch(ColleagueResponse response, String search) {
        return search == null || search.isBlank()
                || response.fullname().toLowerCase()
                .contains(search.toLowerCase());
    }

    public static boolean matchesStatus(ColleagueResponse response, String status) {
        return status == null || status.isBlank()
                || response.status().equalsIgnoreCase(status);
    }

    public static boolean matchesFloor(ColleagueResponse response, Integer floor) {
        return floor == null
                || (response.location() != null
                && response.location().equals(String.valueOf(floor)));
    }

    public static boolean matchesFavorite(
            ColleagueResponse response,
            Boolean favorite
    ) {
        return favorite == null
                || response.isFavorite() == favorite;
    }
}
