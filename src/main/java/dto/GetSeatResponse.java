package dto;

import entities.Seat;
import entities.enums.SeatStatus;

public record GetSeatResponse(
        Integer id,
        String code,
        Integer roomId,
        String status,
        Integer xPosition,
        Integer yPosition,
        Boolean hasMonitor,
        Boolean hasDockingStation,
        Boolean nearWindow,
        Boolean hasStandupDesk,
        String occupiedBy,
        String unavailableReason
) {
    public static GetSeatResponse fromEntity(Seat seat) {
        return fromEntity(seat, null);
    }

    public static GetSeatResponse fromEntity(Seat seat, String occupiedBy) {
        if (seat == null) {
            return null;
        }

        return new GetSeatResponse(
                seat.getId(),
                getCode(seat),
                seat.getRoom() != null ? seat.getRoom().getId() : null,
                occupiedBy != null ? "OCUPAT" : (seat.getStatus() != null ? seat.getStatus().name() : null),
                seat.getXPosition(),
                seat.getYPosition(),
                seat.getHasMonitor(),
                seat.getHasDockingStation(),
                seat.getNearWindow(),
                seat.getHasStandupDesk(),
                occupiedBy,
                occupiedBy == null && seat.getStatus() == SeatStatus.NU_ESTE_REZERVABIL ? "Nu se poate rezerva" : null
        );
    }

    /**
     * The visual maps use stable, human-readable labels.  They are derived
     * from the persisted room and coordinates, so the database remains the
     * single source of truth for the actual seat identity and location.
     */
    private static String getCode(Seat seat) {
        if (seat.getRoom() == null || seat.getRoom().getId() == null) return "S" + seat.getId();

        int room = seat.getRoom().getId();
        int x = seat.getXPosition() == null ? 0 : seat.getXPosition();
        int y = seat.getYPosition() == null ? 0 : seat.getYPosition();

        return switch (room) {
            case 1, 9 -> "" + (char) ('A' + Math.max(0, x - 1)) + y;
            case 2, 10 -> (x == 1 ? "L" : "R") + y;
            case 3, 11 -> x == 1 ? "A" + y : x == 2 ? "C" + y : "S" + y;
            case 4 -> y == 1 ? "T" + x : "B" + x;
            case 5 -> String.valueOf(x);
            case 6 -> y == 1 ? "T" + x : y == 2 ? (x == 0 ? "L1" : "R1") : "B" + x;
            case 7 -> "" + (char) ('A' + Math.max(0, y - 1)) + x;
            case 8 -> switch (x) { case 1 -> "S1"; case 2 -> "D1"; case 3 -> "D3"; case 4 -> "D4"; default -> "D2"; };
            case 12 -> "D" + x + "-" + y;
            case 13 -> y == 1 ? "A" + x : y == 2 ? "B" + x : (x == 0 ? "C1" : "C2");
            case 14 -> y == 1 ? "M" + x : y == 2 ? "W" + x : y == 3 ? "S" + x : "L" + x;
            default -> "S" + seat.getId();
        };
    }
}
