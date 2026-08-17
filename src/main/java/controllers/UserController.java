package controllers;

import dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import services.FavoriteColleagueService;
import services.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final FavoriteColleagueService favoriteColleagueService;

    @GetMapping("/")
    public PageResponse<ColleagueResponse> getColleagues(
            @RequestParam Integer currentUserId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) Boolean favorite,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return userService.getColleagues(
                currentUserId, search, status, floor, favorite, page, size
        );
    }

    @GetMapping("/me/settings")
    public MySettingsResponse getMySettings(@RequestParam Integer userId) {
        return userService.toMySettingsResponse(userId);
    }

    @GetMapping("/me")
    public MyAccountResponse getMyAccount(@RequestParam Integer userId) {
        return userService.getMyAccountResponse(userId);
    }

    // Fix: @PathVariable name matches {id}, currentUserId passed as @RequestParam
    @GetMapping("/{id}")
    public ColleagueProfileResponse getColleague(
            @PathVariable("id") Integer colleagueId,
            @RequestParam Integer currentUserId) {
        return userService.toColleagueProfileResponse(colleagueId, currentUserId);
    }

    // Fix: userId cannot be @PathVariable since it's not in the URL — use @RequestParam
    @PutMapping("/me/favorites/{id}")
    public void addFavorite(
            @PathVariable("id") Integer colleagueId,
            @RequestParam Integer userId) {
        favoriteColleagueService.addFavorite(colleagueId, userId);
    }

    @DeleteMapping("/me/favorites/{id}")
    public void removeFavorite(
            @PathVariable("id") Integer colleagueId,
            @RequestParam Integer userId) {
        favoriteColleagueService.removeFavorite(colleagueId, userId);
    }
}

