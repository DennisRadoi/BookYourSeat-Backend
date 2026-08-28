package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import repositories.*;
import services.EmailService;
import entities.Department;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = com.example.bys.BysApplication.class,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;NON_KEYWORDS=USER",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
                "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "spring.sql.init.mode=always",
                "spring.jpa.defer-datasource-initialization=true",
                "spring.sql.init.schema-locations=classpath:schema-test.sql",
                "spring.sql.init.data-locations=",
                "spring.jpa.open-in-view=false",
                "spring.docker.compose.enabled=false",
                "jwt.secret=test-secret-key-minimum-32-characters-long-ok",
                "jwt.expiration=86400000"
        }
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    protected ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    protected config.OfficeInvitationStatusConstraintMigrator officeInvitationStatusConstraintMigrator;

    @MockitoBean
    protected EmailService emailService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected UserPreferencesRepository userPreferencesRepository;

    @Autowired
    protected DepartmentRepository departmentRepository;

    @Autowired
    protected AddressRepository addressRepository;

    @Autowired
    protected BookingRepository bookingRepository;

    @Autowired
    protected UserNotificationRepository userNotificationRepository;

    @Autowired
    protected NotificationRepository notificationRepository;

    @BeforeEach
    protected void ensureDefaultDepartment() {
        if (departmentRepository.count() == 0) {
            Department dept = new Department();
            dept.setName("IT");
            dept.setDescription("Department for testing");
            departmentRepository.save(dept);
        }
    }

    protected String obtainToken(String email, String password) throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "email", email,
                "password", password
        ));

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        return objectMapper.readTree(responseBody).get("token").asText();
    }

    protected String bearerHeader(String token) {
        return "Bearer " + token;
    }

    protected String registerAndGetToken(String firstName, String lastName, String email, String password) throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "firstName", firstName,
                "lastName", lastName,
                "email", email,
                "password", password,
                "phoneNumber", "+40712345678"
        ));

        MvcResult result = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseBody).get("token").asText();

        userRepository.findByEmail(email).ifPresent(u -> {
            entities.UserPreferences pref = userPreferencesRepository.findByUserId(u.getId())
                    .orElseGet(() -> {
                        entities.UserPreferences p = new entities.UserPreferences();
                        p.setUser(u);
                        return p;
                    });
            if (pref.getBookingConfirmationOnEmail() == null) pref.setBookingConfirmationOnEmail(false);
            if (pref.getReminderBeforeBooking() == null) pref.setReminderBeforeBooking(false);
            if (pref.getNearWindow() == null) pref.setNearWindow(false);
            if (pref.getQuietPlace() == null) pref.setQuietPlace(false);
            userPreferencesRepository.save(pref);
            u.setUserPreferences(pref);
            userRepository.save(u);
        });

        return token;
    }

    protected static final String VALID_PASSWORD = "Test@1234";
    protected static final String TEST_EMAIL = "test@example.com";
}
