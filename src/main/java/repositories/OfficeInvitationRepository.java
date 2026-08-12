package repositories;

import entities.OfficeInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OfficeInvitationRepository extends JpaRepository<OfficeInvitation, Integer> {
    List<OfficeInvitation> findByUserId(Integer userId);

    List<OfficeInvitation> findByAddresseeId(Integer addresseeId);

    Optional<OfficeInvitation> findByProposedDate(LocalDate proposedDate);

    Optional<OfficeInvitation> findByAddresseeIdAndProposedDate(Integer a, LocalDate proposedDate);

    boolean existsByUserIdAndAddresseeIdAndProposedDate(Integer userId, Integer addresseId, LocalDate proposedDate);
}
