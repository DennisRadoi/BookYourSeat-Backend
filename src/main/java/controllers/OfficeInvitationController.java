package controllers;

import dto.CreateInvitationRequest;
import dto.InvitationResponse;
import entities.OfficeInvitation;
import entities.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import services.UserService;

@RestController
@RequestMapping("/office-invitations")
public class OfficeInvitationController {
    private final UserService userService;

    public  OfficeInvitationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<InvitationResponse> createInvitation(
            @Validated @RequestBody CreateInvitationRequest request
            )
    {
        User u = userService.getCurrentUser();
        InvitationResponse response = userService.createInvitation(u.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
