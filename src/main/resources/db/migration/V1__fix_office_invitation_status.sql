ALTER TABLE office_invitation
    DROP CONSTRAINT IF EXISTS office_invitation_status_check;

UPDATE office_invitation SET status = 'IN_ASTEPTARE' WHERE status = 'PENDING';
UPDATE office_invitation SET status = 'ACCEPTATA' WHERE status = 'ACCEPTED';
UPDATE office_invitation SET status = 'REFUZATA' WHERE status IN ('REFUSED', 'REJECTED', 'DECLINED', 'RESPINSA');

ALTER TABLE office_invitation
    ADD CONSTRAINT office_invitation_status_check
    CHECK (status IN ('IN_ASTEPTARE', 'ACCEPTATA', 'REFUZATA'));
