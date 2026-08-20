package controllers;


import dto.AnswerInvitationRequest;
import dto.GetNotificationResponse;
import dto.InvitationResponse;
import entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import services.NotificationService;
import services.UserService;

import java.util.List;

@RestController
@RequestMapping("/users/me/invitations")
@RequiredArgsConstructor
public class InvitationController {
    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<InvitationResponse>>
    getInvitations(@RequestParam(defaultValue = "all") String direction) {
        User u = userService.getCurrentUser();
        return ResponseEntity.status(HttpStatus.OK).body(
                userService.getInvitations(u.getId(), direction)
        );
    }

    @PatchMapping("/{id}/response")
    public ResponseEntity<InvitationResponse>
    updateInvitation(
            @PathVariable Integer id,
            @Validated @RequestBody AnswerInvitationRequest request
            ) {
        User u = userService.getCurrentUser();
        InvitationResponse response = userService.updateInvitationStatus(
                request,
                u.getId(),
                id
        );
        return ResponseEntity.ok(response);
    }
}
