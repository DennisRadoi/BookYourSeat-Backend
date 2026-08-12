package services;

import entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repositories.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilizatorul nu exista."));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilizator cu acest email" + email + " nu exista"));
    }

    public List<User> findByLastName(String lastName) {
        return userRepository.findByLastName(lastName);
    }

    public List<User> findByRole(String role) {
        return userRepository.findByRole(role);
    }

    public User findByLastNameAndFirstName(String lastName, String firstName) {
        return userRepository.findByLastNameAndFirstName(lastName, firstName)
                .orElseThrow(() -> new RuntimeException("Utilizatorul nu exista."));
    }

    public User findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Utilizatorul nu exista."));
    }

    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public List<User> findAllByIsActiveTrue() {
        return userRepository.findAllByIsActiveTrue();
    }

    public List<User> findAllByIsActiveFalse() {
        return userRepository.findAllByIsActiveFalse();
    }

    public List<User> findAllByDepartmentName(String name) {
        return userRepository.findALlByDepartmentName(name);
    }
}