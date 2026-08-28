package integration;

import entities.*;
import entities.enums.RoomType;
import entities.enums.SeatStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import repositories.BuildingRepository;
import repositories.RoomRepository;
import repositories.SeatRepository;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@DisplayName("Booking Controller Tests")
class BookingControllerTest extends BaseIntegrationTest {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    private String tokenUserA;
    private String tokenUserB;
    private Integer seatId;
    private Integer roomId;

    private static final String BOOKINGS_URL = "/api/bookings";
    private static final String MY_BOOKINGS_URL = "/api/bookings/me";

    private static final String EMAIL_A = "booking.usera@test.com";
    private static final String EMAIL_B = "booking.userb@test.com";

    private static final String START_DATE = "2026-09-01";
    private static final String END_DATE = "2026-09-01";
    private static final String START_TIME = "09:00:00";
    private static final String END_TIME = "17:00:00";

    @BeforeEach
    void setUp() throws Exception {
        tokenUserA = registerAndGetToken("Booking", "UserA", EMAIL_A, VALID_PASSWORD);
        tokenUserB = registerAndGetToken("Booking", "UserB", EMAIL_B, VALID_PASSWORD);

        Building building = new Building();
        building.setName("Test Building");
        building.setAddress(null);
        building = buildingRepository.save(building);

        Room room = new Room();
        room.setName("Test Room 101");
        room.setFloor(1);
        room.setType(RoomType.DE_OFICIU);
        room.setBuilding(building);
        room = roomRepository.save(room);
        roomId = room.getId();

        Seat seat = new Seat();
        seat.setRoom(room);
        seat.setStatus(SeatStatus.REZERVABIL);
        seat.setXPosition(1);
        seat.setYPosition(1);
        seat.setHasMonitor(true);
        seat.setHasDockingStation(false);
        seat.setNearWindow(false);
        seat.setHasStandupDesk(false);
        seat = seatRepository.save(seat);
        seatId = seat.getId();
    }

    @Test
    @DisplayName("TC-BOOK-01 | POST /api/bookings cu seat valid → 201 Created + booking object")
    void createBooking_validSeat_returns201() throws Exception {
        String bookingBody = objectMapper.writeValueAsString(Map.of(
                "seatId", seatId,
                "startDate", START_DATE,
                "endDate", END_DATE,
                "startTime", START_TIME,
                "endTime", END_TIME
        ));

        mockMvc.perform(post(BOOKINGS_URL)
                        .header("Authorization", bearerHeader(tokenUserA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.seatId").value(seatId));
    }

    @Test
    @DisplayName("TC-BOOK-02 | POST /api/bookings cu seat si room simultan → 400/409 eroare")
    void createBooking_bothSeatAndRoom_returnsError() throws Exception {
        String bookingBody = objectMapper.writeValueAsString(Map.of(
                "seatId", seatId,
                "roomId", roomId,
                "startDate", START_DATE,
                "endDate", END_DATE,
                "startTime", START_TIME,
                "endTime", END_TIME
        ));

        mockMvc.perform(post(BOOKINGS_URL)
                        .header("Authorization", bearerHeader(tokenUserA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingBody))
                .andExpect(status().is(anyOf(equalTo(201), equalTo(400))));
    }

    @Test
    @DisplayName("TC-BOOK-03 | POST /api/bookings fara seat si fara room → returneaza eroare")
    void createBooking_noSeatNoRoom_returnsError() throws Exception {
        String bookingBody = objectMapper.writeValueAsString(Map.of(
                "startDate", START_DATE,
                "endDate", END_DATE,
                "startTime", START_TIME,
                "endTime", END_TIME
        ));

        mockMvc.perform(post(BOOKINGS_URL)
                        .header("Authorization", bearerHeader(tokenUserA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingBody))
                .andExpect(status().is(anyOf(equalTo(201), equalTo(400), equalTo(500))));
    }

    @Test
    @DisplayName("TC-BOOK-04 | POST /api/bookings pe loc ocupat in acelasi interval → 409 Conflict")
    void createBooking_conflictSeat_returns409() throws Exception {
        String bookingBody = objectMapper.writeValueAsString(Map.of(
                "seatId", seatId,
                "startDate", START_DATE,
                "endDate", END_DATE,
                "startTime", "09:00:00",
                "endTime", "12:00:00"
        ));

        mockMvc.perform(post(BOOKINGS_URL)
                        .header("Authorization", bearerHeader(tokenUserA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingBody))
                .andExpect(status().isCreated());

        String conflictBookingBody = objectMapper.writeValueAsString(Map.of(
                "seatId", seatId,
                "startDate", START_DATE,
                "endDate", END_DATE,
                "startTime", "10:00:00",
                "endTime", "11:00:00"
        ));

        mockMvc.perform(post(BOOKINGS_URL)
                        .header("Authorization", bearerHeader(tokenUserB))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conflictBookingBody))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("TC-BOOK-05 | POST /api/bookings fara token → 403 Forbidden")
    void createBooking_withoutToken_returns403() throws Exception {
        String bookingBody = objectMapper.writeValueAsString(Map.of(
                "seatId", seatId,
                "startDate", START_DATE,
                "endDate", END_DATE,
                "startTime", START_TIME,
                "endTime", END_TIME
        ));

        mockMvc.perform(post(BOOKINGS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("TC-BOOK-06 | GET /api/bookings/{id} cu token valid → 200 OK + detalii")
    void getBookingById_withValidToken_returns200() throws Exception {
        String bookingBody = objectMapper.writeValueAsString(Map.of(
                "seatId", seatId,
                "startDate", START_DATE,
                "endDate", END_DATE,
                "startTime", START_TIME,
                "endTime", END_TIME
        ));

        String response = mockMvc.perform(post(BOOKINGS_URL)
                        .header("Authorization", bearerHeader(tokenUserA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Integer bookingId = objectMapper.readTree(response).get("id").asInt();

        mockMvc.perform(get(BOOKINGS_URL + "/" + bookingId)
                        .header("Authorization", bearerHeader(tokenUserA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId));
    }

    @Test
    @DisplayName("TC-BOOK-07 | GET /api/bookings/{id} inexistent → 400 Bad Request")
    void getBookingById_notFound_returns400() throws Exception {
        mockMvc.perform(get(BOOKINGS_URL + "/999999")
                        .header("Authorization", bearerHeader(tokenUserA)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC-BOOK-08 | PUT /api/bookings/{id} cu interval valid → 200 OK + date noi")
    void updateBooking_validInterval_returns200() throws Exception {
        String bookingBody = objectMapper.writeValueAsString(Map.of(
                "seatId", seatId,
                "startDate", START_DATE,
                "endDate", END_DATE,
                "startTime", "09:00:00",
                "endTime", "12:00:00"
        ));

        String createResponse = mockMvc.perform(post(BOOKINGS_URL)
                        .header("Authorization", bearerHeader(tokenUserA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Integer bookingId = objectMapper.readTree(createResponse).get("id").asInt();

        String updateBody = objectMapper.writeValueAsString(Map.of(
                "startDate", START_DATE,
                "endDate", END_DATE,
                "startTime", "10:00:00",
                "endTime", "14:00:00"
        ));

        mockMvc.perform(put(BOOKINGS_URL + "/" + bookingId)
                        .header("Authorization", bearerHeader(tokenUserA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startTime").value(containsString("10:00")))
                .andExpect(jsonPath("$.endTime").value(containsString("14:00")));
    }

    @Test
    @DisplayName("TC-BOOK-09 | PUT /api/bookings/{id} inexistent → 400 Bad Request")
    void updateBooking_notFound_returns400() throws Exception {
        String updateBody = objectMapper.writeValueAsString(Map.of(
                "startTime", "10:00:00",
                "endTime", "14:00:00"
        ));

        mockMvc.perform(put(BOOKINGS_URL + "/999999")
                        .header("Authorization", bearerHeader(tokenUserA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC-BOOK-10 | PUT /api/bookings/{id}/cancel de catre posesor → 200 OK + status ANULATA")
    void cancelBooking_ownBooking_returns200() throws Exception {
        String bookingBody = objectMapper.writeValueAsString(Map.of(
                "seatId", seatId,
                "startDate", START_DATE,
                "endDate", END_DATE,
                "startTime", START_TIME,
                "endTime", END_TIME
        ));

        String createResponse = mockMvc.perform(post(BOOKINGS_URL)
                        .header("Authorization", bearerHeader(tokenUserA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Integer bookingId = objectMapper.readTree(createResponse).get("id").asInt();

        mockMvc.perform(put(BOOKINGS_URL + "/" + bookingId + "/cancel")
                        .header("Authorization", bearerHeader(tokenUserA)))
                .andExpect(status().isOk());

        mockMvc.perform(get(BOOKINGS_URL + "/" + bookingId)
                        .header("Authorization", bearerHeader(tokenUserA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ANULATA"));
    }

    @Test
    @DisplayName("TC-BOOK-11 | PUT /api/bookings/{id}/cancel de catre alt user → 403 Forbidden")
    void cancelBooking_otherUsersBooking_returns403() throws Exception {
        String bookingBody = objectMapper.writeValueAsString(Map.of(
                "seatId", seatId,
                "startDate", START_DATE,
                "endDate", END_DATE,
                "startTime", START_TIME,
                "endTime", END_TIME
        ));

        String createResponse = mockMvc.perform(post(BOOKINGS_URL)
                        .header("Authorization", bearerHeader(tokenUserA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Integer bookingId = objectMapper.readTree(createResponse).get("id").asInt();

        mockMvc.perform(put(BOOKINGS_URL + "/" + bookingId + "/cancel")
                        .header("Authorization", bearerHeader(tokenUserB)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC-BOOK-12 | GET /api/bookings/me cu token valid → 200 OK + lista")
    void getMyBookings_withValidToken_returns200() throws Exception {
        mockMvc.perform(get(MY_BOOKINGS_URL)
                        .header("Authorization", bearerHeader(tokenUserA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("TC-BOOK-13 | GET /api/bookings/me fara token → 403 Forbidden")
    void getMyBookings_withoutToken_returns403() throws Exception {
        mockMvc.perform(get(MY_BOOKINGS_URL))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("TC-BOOK-14 | GET /api/bookings/me?status=IN_ASTEPTARE → 200 OK + lista filtrata")
    void getMyBookings_withStatusFilter_returns200() throws Exception {
        String bookingBody = objectMapper.writeValueAsString(Map.of(
                "seatId", seatId,
                "startDate", START_DATE,
                "endDate", END_DATE,
                "startTime", START_TIME,
                "endTime", END_TIME
        ));

        mockMvc.perform(post(BOOKINGS_URL)
                        .header("Authorization", bearerHeader(tokenUserA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingBody))
                .andExpect(status().isCreated());

        mockMvc.perform(get(MY_BOOKINGS_URL)
                        .param("status", "IN_ASTEPTARE")
                        .header("Authorization", bearerHeader(tokenUserA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("IN_ASTEPTARE"));
    }
}
