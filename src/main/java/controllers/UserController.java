package controllers;

import dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import services.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    public final UserService userService;

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

    @GetMapping("/{id}")
    public ColleagueProfileResponse getColleague(@PathVariable Integer colleagueId, Integer currentUserId) {
        return userService.toColleagueProfileResponse(colleagueId, currentUserId);
    }
}
