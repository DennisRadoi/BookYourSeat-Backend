package dto;

import entities.Seat;
//import entities.enums.SeatType;

public record GetSeatResponse(
        Integer id,
        Integer roomId,
        String status,
//        SeatType type,
        Integer xPosition,
        Integer yPosition,
        Boolean hasMonitor,
        Boolean hasDockingStation,
        Boolean nearWindow,
        Boolean hasStandupDesk
) {
    public static GetSeatResponse fromEntity(Seat seat) {
        if (seat == null) {
            return null;
        }

        return new GetSeatResponse(
                seat.getId(),
                seat.getRoom() != null ? seat.getRoom().getId() : null,
                seat.getStatus(),
//                seat.getType(),
                seat.getXPosition(),
                seat.getYPosition(),
                seat.getHasMonitor(),
                seat.getHasDockingStation(),
                seat.getNearWindow(),
                seat.getHasStandupDesk()
        );
    }
}
