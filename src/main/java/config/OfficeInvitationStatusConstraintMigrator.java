package config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Hibernate ddl-auto does not rewrite existing CHECK constraints when an enum
 * is renamed. The original values were PENDING/ACCEPTED/REFUSED, so refusing
 * an invitation with REFUZATA violates office_invitation_status_check.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OfficeInvitationStatusConstraintMigrator implements ApplicationRunner {
    private static final String TABLE = "office_invitation";
    private static final String CONSTRAINT = "office_invitation_status_check";

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        List<ConstraintRow> statusChecks = jdbcTemplate.query(
                """
                SELECT con.conname, pg_get_constraintdef(con.oid)
                FROM pg_constraint con
                JOIN pg_class rel ON rel.oid = con.conrelid
                JOIN pg_namespace nsp ON nsp.oid = rel.relnamespace
                WHERE rel.relname = ?
                  AND nsp.nspname = current_schema()
                  AND con.contype = 'c'
                  AND pg_get_constraintdef(con.oid) ILIKE '%status%'
                """,
                (rs, rowNum) -> new ConstraintRow(rs.getString(1), rs.getString(2)),
                TABLE
        );

        if (statusChecks.size() == 1 && isDesiredConstraint(statusChecks.getFirst().definition())) {
            return;
        }

        for (ConstraintRow check : statusChecks) {
            if (!isSafeIdentifier(check.name())) {
                throw new IllegalStateException("Unexpected constraint name: " + check.name());
            }
            jdbcTemplate.execute("ALTER TABLE " + TABLE + " DROP CONSTRAINT IF EXISTS " + check.name());
        }

        jdbcTemplate.update(
                "UPDATE " + TABLE + " SET status = 'IN_ASTEPTARE' WHERE status = 'PENDING'"
        );
        jdbcTemplate.update(
                "UPDATE " + TABLE + " SET status = 'ACCEPTATA' WHERE status = 'ACCEPTED'"
        );
        jdbcTemplate.update(
                """
                UPDATE office_invitation
                SET status = 'REFUZATA'
                WHERE status IN ('REFUSED', 'REJECTED', 'DECLINED', 'RESPINSA')
                """
        );

        jdbcTemplate.execute(
                """
                ALTER TABLE office_invitation
                ADD CONSTRAINT office_invitation_status_check
                CHECK (status IN ('IN_ASTEPTARE', 'ACCEPTATA', 'REFUZATA'))
                """
        );

        log.info("Aligned {} with statuses IN_ASTEPTARE, ACCEPTATA, REFUZATA", CONSTRAINT);
    }

    private static boolean isDesiredConstraint(String definition) {
        String normalized = definition.toUpperCase();
        return normalized.contains("IN_ASTEPTARE")
                && normalized.contains("ACCEPTATA")
                && normalized.contains("REFUZATA")
                && !normalized.contains("PENDING")
                && !normalized.contains("ACCEPTED")
                && !normalized.contains("REFUSED");
    }

    private static boolean isSafeIdentifier(String name) {
        return name != null && name.matches("[A-Za-z_][A-Za-z0-9_]*");
    }

    private record ConstraintRow(String name, String definition) {
    }
}
