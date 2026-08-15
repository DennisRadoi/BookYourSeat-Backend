package controllers;

import dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import services.FavoriteColleagueService;
import services.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    public final UserService userService;
    public final FavoriteColleagueService favoriteColleagueService;

    @GetMapping // e ok
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

    @GetMapping("/me/settings") // e ok
    public MySettingsResponse getMySettings(@RequestParam Integer userId) {
        return userService.toMySettingsResponse(userId);
    }

    @GetMapping("/me") // e ok
    public MyAccountResponse getMyAccount(@RequestParam Integer userId) {
        return userService.getMyAccountResponse(userId);
    }

    @GetMapping("/{colleagueId}") // e ok
    public ColleagueProfileResponse getColleague(@PathVariable Integer colleagueId, @RequestParam Integer currentUserId) {
        return userService.toColleagueProfileResponse(colleagueId, currentUserId);
    }

    @PutMapping("me/favorites/{colleagueId}") // e ok dar trebuie sa adaug status code 201
    public void addFavorite(@PathVariable Integer colleagueId, @RequestParam Integer userId) {
        favoriteColleagueService.addFavorite(colleagueId, userId);
    }

    @DeleteMapping("me/favorite/{colleagueId}") // e ok
    public void removeFavorite(@PathVariable Integer colleagueId, @RequestParam Integer userId) {
        favoriteColleagueService.removeFavorite(colleagueId, userId);
    }

    @PatchMapping("/me") // e ok trb sa actualizez status code
    public MyAccountResponse updateProfile(@RequestParam Integer currentUserId,
                                           @Validated @RequestBody UpdateMyAccountRequest request) {
        return userService.updateProfile(currentUserId, request);
    }
}