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
    public final UserService userService;
    public final FavoriteColleagueService favoriteColleagueService;

    @GetMapping
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

    @GetMapping("/{colleagueId}")
    public ColleagueProfileResponse getColleague(@PathVariable Integer colleagueId, @RequestParam Integer currentUserId) {
        return userService.toColleagueProfileResponse(colleagueId, currentUserId);
    }

    @PutMapping("me/favorites/{id}")
    public void addFavorite(@PathVariable Integer colleagueId, @PathVariable Integer userId) {
        favoriteColleagueService.addFavorite(colleagueId, userId);
    }

    @DeleteMapping("me/favorite/{id}")
    public void removeFavorite(@PathVariable Integer colleagueId, @PathVariable Integer userId) {
        favoriteColleagueService.removeFavorite(colleagueId, userId);
    }
}
