package security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import repositories.UserRepository;
import security.dto.LoginRequest;
import security.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import security.dto.RegisterRequest;
import

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        UserDetails user =
                userDetailsService.loadUserByUsername(
                        request.email()
                );

        String token =
                jwtService.generateToken(user);

        return new LoginResponse(token);
    }

    public LoginResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());

        user.setPasswordHash(
                passwordEncoder.encode(request.password())
        );

        user.setRole("ROLE_USER");
        user.setIsActive(true);

        userRepository.save(user);

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        user.getEmail()
                );

        String token =
                jwtService.generateToken(userDetails);

        return new LoginResponse(token);
    }
}