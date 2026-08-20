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
    private final UserService userService;
    private final FavoriteColleagueService favoriteColleagueService;

    @GetMapping
    public PageResponse<ColleagueResponse> getColleagues(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) String building,
            @RequestParam(required = false) Boolean favorite,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Integer currentUserId = userService.getCurrentUser().getId();
        return userService.getColleagues(
                currentUserId, search, status, floor, building, favorite, page, size
        );
    }

    @GetMapping("/me/settings")
    public MySettingsResponse getMySettings() {
        Integer userId = userService.getCurrentUser().getId();
        return userService.toMySettingsResponse(userId);
    }

    @GetMapping("/me")
    public MyAccountResponse getMyAccount() {
        Integer userId = userService.getCurrentUser().getId();
        return userService.getMyAccountResponse(userId);
    }

    @GetMapping("/{colleagueId}")
    public ColleagueProfileResponse getColleague(@PathVariable Integer colleagueId) {
        Integer currentUserId = userService.getCurrentUser().getId();
        return userService.toColleagueProfileResponse(colleagueId, currentUserId);
    }

    @PostMapping("/me/favorites/{colleagueId}")
    public ResponseEntity<String> addFavorite(@PathVariable Integer colleagueId) {
        Integer userId = userService.getCurrentUser().getId();
        boolean created = favoriteColleagueService.addFavorite(colleagueId, userId);
        if (created) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Added favorite to currentUser list.");
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Colleague is already in favorites.");
    }

    @DeleteMapping("/me/favorites/{colleagueId}")
    public ResponseEntity<String> removeFavorite(@PathVariable Integer colleagueId) {
        Integer userId = userService.getCurrentUser().getId();
        boolean deleted = favoriteColleagueService.removeFavorite(colleagueId, userId);

        if (deleted) {
            User colleague = userService.findById(colleagueId);
            return ResponseEntity.ok("Deleted favorite " + colleague.getFirstName()
                    + " " + colleague.getLastName() + " from list.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Colleague is not in your favorites list.");
    }

    @PatchMapping("/me")
    public MyAccountResponse updateProfile(
            @Validated @RequestBody UpdateMyAccountRequest request) {
        Integer currentUserId = userService.getCurrentUser().getId();
        return userService.updateProfile(currentUserId, request);
    }

    @PatchMapping("/me/preferences")
    public MyAccountResponse updateAccountPreferences(
            @Validated @RequestBody UpdateAccountPreferencesRequest request) {
        Integer currentUserId = userService.getCurrentUser().getId();
        return userService.updateAccountPagePreferences(currentUserId, request);
    }

    @PatchMapping("me/settings/preferences")
    public MySettingsResponse updateSettingsPreferences(
            @Validated @RequestBody UpdateSettingsPreferencesRequest request) {
        Integer currentUserId = userService.getCurrentUser().getId();
        return userService.updateSettings(currentUserId, request);
    }

    @GetMapping("/me/favorites")
    public List<ColleagueResponse> getMyFavorites() {
        Integer currentUserId = userService.getCurrentUser().getId();
        return userService.getListOfFavorites(currentUserId);
    }

    @PostMapping("/{colleagueId}/office-invitation")
    public ResponseEntity<InvitationResponse> createInvitation(
            @PathVariable Integer colleagueId,
            @Validated @RequestBody CreateInvitationRequest request
    )
    {
        User u = userService.getCurrentUser();
        InvitationResponse response = userService.createInvitation(u.getId(), request, colleagueId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/me/change-password")
    public ResponseEntity<String> changePassword(
            @Validated @RequestBody ChangePasswordRequest request
    ) {
        Integer currentUserId = userService.getCurrentUser().getId();
        userService.changePassword(request, currentUserId);
        return ResponseEntity.ok().body("Password has been modified.");
    }
}
