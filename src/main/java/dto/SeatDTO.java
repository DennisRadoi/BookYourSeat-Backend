package dto;

import entities.Seat;
import entities.SeatType;

public record SeatDTO(
        Integer id,
        Integer roomId,
        String status,
        SeatType type,
        Integer xPosition,
        Integer yPosition,
        Boolean hasMonitor,
        Boolean hasDockingStation,
        Boolean nearWindow,
        Boolean hasStandupDesk
) {
    public static SeatDTO fromEntity(Seat seat) {
        if (seat == null) {
            return null;
        }

        return new SeatDTO(
                seat.getId(),
                seat.getRoomId(),
                seat.getStatus(),
                seat.getType(),
                seat.getXPosition(),
                seat.getYPosition(),
                seat.getHasMonitor(),
                seat.getHasDockingStation(),
                seat.getNearWindow(),
                seat.getHasStandupDesk()
        );
    }
}
