package controllers;

import services.SeatService;
import dto.SeatDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class SeatController {

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @GetMapping("/seats/{id}")
    public ResponseEntity<SeatDTO> getSeatById(@PathVariable Integer id) {
        return ResponseEntity.ok(SeatDTO.fromEntity(seatService.getSeatById(id)));
    }

    @GetMapping("/rooms/room/{id}/seats")
    public ResponseEntity<List<SeatDTO>> getSeatsByRoom(@PathVariable Integer id) {
        return ResponseEntity.ok(seatService.getSeatsByRoom(id).stream()
                .map(SeatDTO::fromEntity)
                .collect(Collectors.toList()));
    }

    @GetMapping("/seats")
    public ResponseEntity<List<SeatDTO>> searchSeats(
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "nearWindow", required = false) Boolean nearWindow,
            @RequestParam(name = "hasMonitor", required = false) Boolean hasMonitor,
            @RequestParam(name = "hasStandupDesk", required = false) Boolean hasStandupDesk,
            @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(name = "startTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam(name = "endTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {

        return ResponseEntity.ok(seatService.searchAvailableSeats(type, nearWindow, hasMonitor, hasStandupDesk, date, startTime, endTime)
                .stream()
                .map(SeatDTO::fromEntity)
                .collect(Collectors.toList()));
    }
}