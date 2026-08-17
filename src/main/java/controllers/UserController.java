package controllers;

import dto.*;
import entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import services.FavoriteColleagueService;
import services.UserService;

import java.util.List;

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

    @PostMapping("me/favorites/{colleagueId}")
    public ResponseEntity<String> addFavorite(@PathVariable Integer colleagueId,
                                            @RequestParam Integer userId) {

        boolean created = favoriteColleagueService.addFavorite(colleagueId, userId);

        if (created) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Added favorite to currentUser list.");
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Colleague is already in favorites.");
    }

    @DeleteMapping("/me/favorites/{colleagueId}") // e ok
    public ResponseEntity<String> removeFavorite(@PathVariable Integer colleagueId, @RequestParam Integer userId) {
        boolean deleted = favoriteColleagueService.removeFavorite(colleagueId, userId);

        User colleague = userService.findById(colleagueId);

        if (deleted) {
            return ResponseEntity.status(HttpStatus.OK).body("Deleted favorite with name " + colleague.getFirstName()
                    + " " + colleague.getLastName() + " from currentUser list.");
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body(colleague.getLastName() + " " + colleague.getFirstName() +
                " is already a favortie of currentUser");
    }

    @PatchMapping("/me") // e ok trb sa actualizez status code
    public MyAccountResponse updateProfile(@RequestParam Integer currentUserId,
                                           @Validated @RequestBody UpdateMyAccountRequest request) {
        return userService.updateProfile(currentUserId, request);
    }

    @PatchMapping("/me/preferences") // e ok
    public MyAccountResponse updateAccountPreferences(@RequestParam Integer currentUserId,
                                                      @Validated @RequestBody UpdateAccountPreferencesRequest request) {
        return userService.updateAccountPagePreferences(currentUserId, request);
    }
    @PatchMapping("me/settings/preferences") // e ok
    public MySettingsResponse updateSettingsPreferences(@RequestParam Integer currentUserId,
                                                       @Validated @RequestBody UpdateSettingsPreferencesRequest request) {
        return userService.updateSettings(currentUserId, request);
    }
    @GetMapping("/me/favorites")
    public List<ColleagueResponse> GetMyFavorites(@RequestParam Integer currentUserId) {
        return userService.getListOfFavorites(currentUserId);
    }
}