package controllers;

import dto.ColleagueResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import services.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    public final UserService userService;

    @GetMapping
    List<ColleagueResponse> getColleagues(@RequestParam Integer currentUserId){
        return userService.getColleagues(currentUserId);
    }
}
