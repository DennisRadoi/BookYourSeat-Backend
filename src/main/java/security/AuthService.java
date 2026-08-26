package security;

import entities.Address;
import entities.Department;
import entities.User;
import entities.UserPreferences;
import entities.enums.AddressType;
import exceptions.EmailAlreadyExistsException;
import exceptions.LoginFailedException;
import exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repositories.AddressRepository;
import repositories.DepartmentRepository;
import repositories.UserPreferencesRepository;
import repositories.UserRepository;
import security.dto.*;
import services.EmailService;
import utils.InputValidation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    private final UserRepository userRepository;
    private final UserPreferencesRepository userPreferencesRepository;
    private final DepartmentRepository departmentRepository;
    private final AddressRepository addressRepository;

    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public LoginResponse login(LoginRequest request) {
        if (request.email() == null || userRepository.findByEmail(request.email()).isEmpty()) {
            throw new LoginFailedException("Utilizatorul nu există");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (BadCredentialsException ex) {
            throw new LoginFailedException("Parola e incorectă");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());

        String token = jwtService.generateToken(userDetails);

        return new LoginResponse(token);
    }

    @Transactional
    public LoginResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }
        InputValidation.requireValidPassword(request.password());
        if (request.phoneNumber() != null && !request.phoneNumber().isBlank()) {
            InputValidation.requireValidPhoneNumber(request.phoneNumber());
        }

//        Department department = request.departmentId() == null
//                ? departmentRepository.findAll().stream().findFirst()
//                    .orElseThrow(() -> new IllegalStateException("No department is configured."))
//                : departmentRepository.findById(request.departmentId())
//                    .orElseThrow(() -> new ResourceNotFoundException("Department", request.departmentId()));
        Department department = departmentRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No department is configured."));
//        Address address = request.addressId() == null
//                ? addressRepository.findAll().stream().findFirst()
//                    .orElseThrow(() -> new IllegalStateException("No address is configured."))
//                : addressRepository.findById(request.addressId())
//                    .orElseThrow(() -> new ResourceNotFoundException("Address", request.addressId()));
        Address address = new Address();
        address.setType(AddressType.DE_DOMICILIU);
        address.setNumber("");
        address.setStreet("");
        address.setPostalCode("");
        addressRepository.save(address);

        User user = new User();

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber() == null || request.phoneNumber().isBlank()
                ? null
                : request.phoneNumber());

        user.setPasswordHash(passwordEncoder.encode(request.password()));

        user.setRole("ROLE_USER");
        user.setIsActive(true);

        user.setProfilePhoto(null);

        user.setEmploymentDate(LocalDate.now());

        user.setDepartment(department);
        user.setAddress(address);

        userRepository.save(user);

        UserPreferences preferences = new UserPreferences();
        preferences.setUser(user);
        preferences.setBookingConfirmationOnEmail(false);
        preferences.setReminderBeforeBooking(false);
        preferences.setNearWindow(false);
        preferences.setQuietPlace(false);
        userPreferencesRepository.save(preferences);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        String token = jwtService.generateToken(userDetails);

        return new LoginResponse(token);
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.email()));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));

        userRepository.save(user);

        String resetLink = "http://localhost:5173/reset-password?token=" + token;

        Map<String, Object> variables = Map.of(
                "firstName", user.getFirstName(),
                "resetLink", resetLink
        );

        emailService.sendEmail(user.getEmail(), "Resetare Parolă", "reset-password", variables);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetToken(request.token())
                .orElseThrow(() -> new IllegalArgumentException("Token-ul de resetare este invalid."));

        if (user.getResetTokenExpiry() == null || LocalDateTime.now().isAfter(user.getResetTokenExpiry())) {
            throw new IllegalArgumentException("Token-ul de resetare a expirat.");
        }

        InputValidation.requireValidPassword(request.newPassword());
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);

        userRepository.save(user);
    }
}
