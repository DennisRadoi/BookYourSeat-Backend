package controllers;

import dto.AnalyticsBookingsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import services.AnalyticsService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;
    @GetMapping("/bookings/total")
    public ResponseEntity<AnalyticsBookingsResponse> getTotalBookings(
            @RequestParam int year,
            @RequestParam int month
    ) {
        long total = analyticsService.getTotalBookingsForMonth(year, month);

        return ResponseEntity.ok(
                new AnalyticsBookingsResponse(year, month, total)
        );
    }
}
