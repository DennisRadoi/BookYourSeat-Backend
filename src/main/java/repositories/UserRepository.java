package repositories;

import entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
    List<User> findByLastName(String lastName);
    Optional<User> findByLastNameAndFirstName(String lastName, String firstName);
    List<User> findByRole(String role);
    Optional<User> findByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    List<User> findAllByIsActiveTrue();
    List<User> findAllByIsActiveFalse();
    @Query(value = """
        SELECT u.*
                FROM USERS u
                JOIN DEPARTMENT d ON d.id = u.department_id
                WHERE d.name = :name
        """
    , nativeQuery = true)
    List<User> findALlByDepartmentName(@Param("name") String name);
}
