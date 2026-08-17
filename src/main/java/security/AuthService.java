package security;

import entities.Address;
import entities.Department;
import entities.User;
import entities.UserPreferences;
import exceptions.EmailAlreadyExistsException;
import exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repositories.AddressRepository;
import repositories.DepartmentRepository;
import repositories.UserPreferencesRepository;
import repositories.UserRepository;
import security.dto.LoginRequest;
import security.dto.LoginResponse;
import security.dto.RegisterRequest;

import java.time.LocalDate;

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

    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());

        String token = jwtService.generateToken(userDetails);

        return new LoginResponse(token);
    }

    @Transactional
    public LoginResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", request.departmentId()));

        Address address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address", request.addressId()));

        User user = new User();

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());

        user.setPasswordHash(passwordEncoder.encode(request.password()));

        user.setRole("ROLE_USER");
        user.setIsActive(true);

        user.setProfilePhoto("default.png");

        user.setEmploymentDate(LocalDate.now());

        user.setDepartment(department);
        user.setAddress(address);

        userRepository.save(user);

        // Creeaza preferinte default pentru noul user
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
}