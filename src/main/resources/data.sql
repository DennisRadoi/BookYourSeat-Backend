INSERT INTO DEPARTMENT (name, description) VALUES
                                               ('Engineering', 'Echipa de dezvoltare software'),
                                               ('Design', 'Echipa de UI si UX'),
                                               ('QA', 'Echipa de testare'),
                                               ('HR', 'Echipa de resurse umane'),
                                               ('Management', 'Echipa de management');


INSERT INTO COUNTY (name) VALUES
                              ('Bucuresti'),
                              ('Prahova'),
                              ('Cluj'),
                              ('Iasi'),
                              ('Timis');


INSERT INTO LOCALITY (name, county_id) VALUES
                                           ('Sector 1', 1),
                                           ('Sector 2', 1),
                                           ('Sector 6', 1),
                                           ('Ploiesti', 2),
                                           ('Cluj-Napoca', 3);


INSERT INTO ADDRESS (street, number, floor, apartment_block, postal_code, locality_id, type) VALUES
                                                                                                 ('Aleea Tibles', '24', NULL, NULL, '060111', 3, 'DE_OFICIU'),
                                                                                                 ('Aleea Tibles', '26', NULL, NULL, '060112', 3, 'DE_OFICIU'),
                                                                                                 ('Strada Nitu Vasile', '58', 2, 'A', '040321', 1, 'DE_DOMICILIU'),
                                                                                                 ('Bulevardul Republicii', '10', 4, 'B', '100001', 4, 'DE_DOMICILIU'),
                                                                                                 ('Strada Memorandumului', '5', 1, 'C', '400114', 5, 'DE_DOMICILIU');


INSERT INTO USERS (
    last_name, first_name, email, department_id, role, phone_number,
    address_id, profile_photo, employment_date, is_active, password_hash
) VALUES
      ('Ciupitu', 'Claudiu', 'claudiu.ciupitu@bys.ro', 1, 'Backend Developer', '0711111111', 3, NULL, '2024-03-01', TRUE, 'hash parola 1'),
      ('Bituleanu', 'Ruxandra', 'ruxandra.bituleanu@bys.ro', 2, 'Product Designer', '0722222222', 4, NULL, '2023-09-15', TRUE, 'hash parola 2'),
      ('Popescu', 'Ana', 'ana.popescu@bys.ro', 2, 'UI UX Designer', '0733333333', 5, NULL, '2024-01-10', TRUE, 'hash parola 3'),
      ('Ionescu', 'Mihai', 'mihai.ionescu@bys.ro', 1, 'Backend Engineer', '0744444444', 3, NULL, '2022-11-20', TRUE, 'hash parola 4'),
      ('Radu', 'Denis', 'denis.radu@bys.ro', 3, 'QA Engineer', '0755555555', 4, NULL, '2025-02-03', TRUE, 'hash parola 5');


INSERT INTO FAVORITE_COLLEAGUE (user_id, favorite_colleague_id) VALUES
                                                                    (1, 2),
                                                                    (1, 3),
                                                                    (2, 1),
                                                                    (3, 4),
                                                                    (4, 5);


INSERT INTO OFFICE_INVITATION (user_id, addressee_id, message, proposed_date) VALUES
                                                                                  (1, 2, 'Hai la birou maine.', '2026-08-15'),
                                                                                  (2, 3, 'Lucram impreuna la etajul 1?', '2026-08-16'),
                                                                                  (3, 1, 'Te invit la birou joi.', '2026-08-20'),
                                                                                  (4, 5, 'Rezervam locuri apropiate?', '2026-08-18'),
                                                                                  (5, 1, 'Hai la sediul T2.', '2026-08-19');



-- ============================================================
-- SEED DATA — generat din mock-ul FE (locations.ts)
-- Structura: buildings → rooms → seats
-- Toate locurile au status REZERVABIL (disponibil) implicit.
-- ============================================================

-- ──────────────────────────────────────────────────────────────
-- BUILDINGS
-- ──────────────────────────────────────────────────────────────
INSERT INTO building (name) VALUES
                                ('Corp T1'),
                                ('Corp T2');

-- ──────────────────────────────────────────────────────────────
-- ROOMS
-- Coloane: id, name, floor, type, building_id
-- type: DE_OFICIU = birouri, DE_CONFERINTA = conferinte/relaxare/events
-- ──────────────────────────────────────────────────────────────
-- ──────────────────────────────────────────────────────────────
-- ROOMS (fără id)
-- Coloane: name, floor, type, building_id
-- ──────────────────────────────────────────────────────────────
INSERT INTO room (name, floor, type, building_id) VALUES
                                                      -- Corp T1 — Parter (floor 0)
                                                      ('Birouri Parter',    0, 'DE_OFICIU',      1),
                                                      ('Stand-Up Desk',     0, 'DE_OFICIU',      1),
                                                      ('Sală de Relaxare',  0, 'DE_CONFERINTA',  1),

                                                      -- Corp T1 — Etaj 1 (floor 1)
                                                      ('Sală Evenimente',   1, 'DE_CONFERINTA',  1),
                                                      ('Side Evenimente',   1, 'DE_CONFERINTA',  1),
                                                      ('La Terasă',         1, 'DE_CONFERINTA',  1),

                                                      -- Corp T1 — Etaj 2 (floor 2)
                                                      ('Sala Gaming',       2, 'DE_CONFERINTA',  1),
                                                      ('Sala Tenis',        2, 'DE_CONFERINTA',  1),

                                                      -- Corp T2 — Parter (floor 0)
                                                      ('Birouri Parter',    0, 'DE_OFICIU',      2),
                                                      ('Stand-Up Desk',     0, 'DE_OFICIU',      2),
                                                      ('Sală de Relaxare',  0, 'DE_CONFERINTA',  2),

                                                      -- Corp T2 — Etaj 1 (floor 1)
                                                      ('Birou Open Space',  1, 'DE_OFICIU',      2),
                                                      ('404',               1, 'DE_CONFERINTA',  2),

                                                      -- Corp T2 — Etaj 2 (floor 2)
                                                      ('Sala Birou - Etaj 2', 2, 'DE_OFICIU',   2);

-- ──────────────────────────────────────────────────────────────
-- SEATS
-- Coloane: id, room_id, status, x_position, y_position,
--          has_monitor, has_docking_station, has_standup_desk, near_window
--
-- Convenție poziții:
--   x_position = coloana (A=1, B=2, C=3 ... sau numărul biroului)
--   y_position = rândul / locul pe birou (1=stânga/sus, 2=dreapta/jos)
--
-- Status: REZERVABIL = disponibil, NU_ESTE_REZERVABIL = ocupat
-- ──────────────────────────────────────────────────────────────

-- ════════════════════════════════════════════════════════════════
-- ROOM 100: Birouri Parter — Corp T1
-- 12 locuri: A1,A2, B1,B2, C1,C2, D1,D2, E1,E2, F1,F2
-- ════════════════════════════════════════════════════════════════
-- ──────────────────────────────────────────────────────────────
-- SEATS (fără id)
-- Coloane: room_id, status, x_position, y_position,
--          has_monitor, has_docking_station, has_standup_desk, near_window
-- ──────────────────────────────────────────────────────────────

-- ROOM 1: Birouri Parter — Corp T1
INSERT INTO seat (room_id, status, x_position, y_position, has_monitor, has_docking_station, has_standup_desk, near_window) VALUES
                                                                                                                                (1, 'NU_ESTE_REZERVABIL', 1, 1, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (1, 'REZERVABIL',         1, 2, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (1, 'REZERVABIL',         2, 1, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (1, 'NU_ESTE_REZERVABIL', 2, 2, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (1, 'REZERVABIL',         3, 1, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (1, 'REZERVABIL',         3, 2, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (1, 'NU_ESTE_REZERVABIL', 4, 1, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (1, 'REZERVABIL',         4, 2, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (1, 'REZERVABIL',         5, 1, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (1, 'REZERVABIL',         5, 2, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (1, 'REZERVABIL',         6, 1, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (1, 'REZERVABIL',         6, 2, TRUE,  FALSE, FALSE, FALSE);

-- ROOM 2: Stand-Up Desk — Corp T1
INSERT INTO seat (room_id, status, x_position, y_position, has_monitor, has_docking_station, has_standup_desk, near_window) VALUES
                                                                                                                                (2, 'REZERVABIL',         1, 1, FALSE, FALSE, TRUE, FALSE),
                                                                                                                                (2, 'REZERVABIL',         1, 2, FALSE, FALSE, TRUE, FALSE),
                                                                                                                                (2, 'NU_ESTE_REZERVABIL', 1, 3, FALSE, FALSE, TRUE, FALSE),
                                                                                                                                (2, 'REZERVABIL',         1, 4, FALSE, FALSE, TRUE, FALSE),
                                                                                                                                (2, 'REZERVABIL',         2, 1, FALSE, FALSE, TRUE, FALSE),
                                                                                                                                (2, 'REZERVABIL',         2, 2, FALSE, FALSE, TRUE, FALSE),
                                                                                                                                (2, 'REZERVABIL',         2, 3, FALSE, FALSE, TRUE, FALSE),
                                                                                                                                (2, 'REZERVABIL',         2, 4, FALSE, FALSE, TRUE, FALSE);

-- ROOM 3: Sală de Relaxare — Corp T1
INSERT INTO seat (room_id, status, x_position, y_position, has_monitor, has_docking_station, has_standup_desk, near_window) VALUES
                                                                                                                                (3, 'REZERVABIL',         1, 1, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (3, 'NU_ESTE_REZERVABIL', 1, 2, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (3, 'REZERVABIL',         1, 3, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (3, 'REZERVABIL',         2, 1, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (3, 'REZERVABIL',         2, 2, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (3, 'REZERVABIL',         2, 3, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (3, 'REZERVABIL',         2, 4, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (3, 'REZERVABIL',         3, 1, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (3, 'REZERVABIL',         3, 2, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (3, 'REZERVABIL',         3, 3, FALSE, FALSE, FALSE, FALSE);

-- ROOM 4: Sală Evenimente — Corp T1 Etaj 1
INSERT INTO seat (room_id, status, x_position, y_position, has_monitor, has_docking_station, has_standup_desk, near_window) VALUES
                                                                                                                                (4, 'REZERVABIL',         1,  1, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (4, 'REZERVABIL',         2,  1, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (4, 'REZERVABIL',         3,  1, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (4, 'REZERVABIL',         4,  1, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (4, 'NU_ESTE_REZERVABIL', 5,  1, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (4, 'REZERVABIL',         6,  1, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (4, 'REZERVABIL',         7,  1, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (4, 'REZERVABIL',         8,  1, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (4, 'REZERVABIL',         9,  1, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (4, 'REZERVABIL',         10, 1, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (4, 'REZERVABIL',         1,  2, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (4, 'REZERVABIL',         2,  2, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (4, 'NU_ESTE_REZERVABIL', 3,  2, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (4, 'REZERVABIL',         4,  2, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (4, 'REZERVABIL',         5,  2, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (4, 'REZERVABIL',         6,  2, FALSE, FALSE, FALSE, FALSE);

-- ROOM 5: Side Evenimente — Corp T1 Etaj 1
INSERT INTO seat (room_id, status, x_position, y_position, has_monitor, has_docking_station, has_standup_desk, near_window) VALUES
                                                                                                                                (5, 'REZERVABIL',         1, 1, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (5, 'NU_ESTE_REZERVABIL', 2, 1, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (5, 'REZERVABIL',         3, 1, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (5, 'REZERVABIL',         4, 1, FALSE, FALSE, FALSE, FALSE);

-- ROOM 6: La Terasă — Corp T1 Etaj 1
INSERT INTO seat (room_id, status, x_position, y_position, has_monitor, has_docking_station, has_standup_desk, near_window) VALUES
                                                                                                                                (6, 'REZERVABIL',         1, 1, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (6, 'REZERVABIL',         2, 1, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (6, 'REZERVABIL',         3, 1, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (6, 'REZERVABIL',         4, 1, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (6, 'REZERVABIL',         0, 2, FALSE, FALSE, FALSE, TRUE),
                                                                                                                                (6, 'REZERVABIL',         5, 2, FALSE, FALSE, FALSE, TRUE),
                                                                                                                                (6, 'REZERVABIL',         1, 3, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (6, 'NU_ESTE_REZERVABIL', 2, 3, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (6, 'REZERVABIL',         3, 3, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (6, 'REZERVABIL',         4, 3, FALSE, FALSE, FALSE, FALSE);

-- ROOM 7: Sala Gaming — Corp T1 Etaj 2
INSERT INTO seat (room_id, status, x_position, y_position, has_monitor, has_docking_station, has_standup_desk, near_window) VALUES
                                                                                                                                (7, 'REZERVABIL',         1, 1, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (7, 'REZERVABIL',         2, 1, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (7, 'REZERVABIL',         3, 1, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (7, 'REZERVABIL',         4, 1, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (7, 'REZERVABIL',         1, 2, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (7, 'REZERVABIL',         2, 2, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (7, 'REZERVABIL',         3, 2, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (7, 'REZERVABIL',         4, 2, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (7, 'REZERVABIL',         1, 3, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (7, 'NU_ESTE_REZERVABIL', 2, 3, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (7, 'REZERVABIL',         3, 3, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (7, 'REZERVABIL',         4, 3, FALSE, FALSE, FALSE, FALSE);

-- ROOM 8: Sala Tenis — Corp T1 Etaj 2
INSERT INTO seat (room_id, status, x_position, y_position, has_monitor, has_docking_station, has_standup_desk, near_window) VALUES
                                                                                                                                (8, 'REZERVABIL',         1, 1, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (8, 'REZERVABIL',         2, 1, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (8, 'REZERVABIL',         3, 1, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (8, 'NU_ESTE_REZERVABIL', 4, 1, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (8, 'REZERVABIL',         5, 1, FALSE, FALSE, FALSE, FALSE);

-- ROOM 9: Birouri Parter — Corp T2
INSERT INTO seat (room_id, status, x_position, y_position, has_monitor, has_docking_station, has_standup_desk, near_window) VALUES
                                                                                                                                (9, 'NU_ESTE_REZERVABIL', 1, 1, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (9, 'REZERVABIL',         1, 2, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (9, 'REZERVABIL',         2, 1, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (9, 'NU_ESTE_REZERVABIL', 2, 2, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (9, 'REZERVABIL',         3, 1, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (9, 'REZERVABIL',         3, 2, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (9, 'NU_ESTE_REZERVABIL', 4, 1, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (9, 'REZERVABIL',         4, 2, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (9, 'REZERVABIL',         5, 1, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (9, 'REZERVABIL',         5, 2, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (9, 'REZERVABIL',         6, 1, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (9, 'REZERVABIL',         6, 2, TRUE,  FALSE, FALSE, FALSE);

-- ROOM 10: Stand-Up Desk — Corp T2
INSERT INTO seat (room_id, status, x_position, y_position, has_monitor, has_docking_station, has_standup_desk, near_window) VALUES
                                                                                                                                (10, 'REZERVABIL',         1, 1, FALSE, FALSE, TRUE, FALSE),
                                                                                                                                (10, 'REZERVABIL',         1, 2, FALSE, FALSE, TRUE, FALSE),
                                                                                                                                (10, 'NU_ESTE_REZERVABIL', 1, 3, FALSE, FALSE, TRUE, FALSE),
                                                                                                                                (10, 'REZERVABIL',         1, 4, FALSE, FALSE, TRUE, FALSE),
                                                                                                                                (10, 'REZERVABIL',         2, 1, FALSE, FALSE, TRUE, FALSE),
                                                                                                                                (10, 'REZERVABIL',         2, 2, FALSE, FALSE, TRUE, FALSE),
                                                                                                                                (10, 'REZERVABIL',         2, 3, FALSE, FALSE, TRUE, FALSE),
                                                                                                                                (10, 'REZERVABIL',         2, 4, FALSE, FALSE, TRUE, FALSE);

-- ROOM 11: Sală de Relaxare — Corp T2
INSERT INTO seat (room_id, status, x_position, y_position, has_monitor, has_docking_station, has_standup_desk, near_window) VALUES
                                                                                                                                (11, 'REZERVABIL',         1, 1, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (11, 'NU_ESTE_REZERVABIL', 1, 2, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (11, 'REZERVABIL',         1, 3, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (11, 'REZERVABIL',         2, 1, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (11, 'REZERVABIL',         2, 2, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (11, 'REZERVABIL',         2, 3, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (11, 'REZERVABIL',         2, 4, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (11, 'REZERVABIL',         3, 1, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (11, 'REZERVABIL',         3, 2, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (11, 'REZERVABIL',         3, 3, FALSE, FALSE, FALSE, FALSE);

-- ROOM 12: Birou Open Space — Corp T2 Etaj 1
INSERT INTO seat (room_id, status, x_position, y_position, has_monitor, has_docking_station, has_standup_desk, near_window) VALUES
                                                                                                                                (12, 'REZERVABIL', 1,  1, TRUE, TRUE, FALSE, FALSE),
                                                                                                                                (12, 'REZERVABIL', 1,  2, TRUE, TRUE, FALSE, FALSE),
                                                                                                                                (12, 'REZERVABIL', 2,  1, TRUE, TRUE, FALSE, FALSE),
                                                                                                                                (12, 'REZERVABIL', 2,  2, TRUE, TRUE, FALSE, FALSE),
                                                                                                                                (12, 'REZERVABIL', 3,  1, TRUE, TRUE, FALSE, FALSE),
                                                                                                                                (12, 'REZERVABIL', 3,  2, TRUE, TRUE, FALSE, FALSE),
                                                                                                                                (12, 'REZERVABIL', 4,  1, TRUE, TRUE, FALSE, FALSE),
                                                                                                                                (12, 'REZERVABIL', 4,  2, TRUE, TRUE, FALSE, FALSE),
                                                                                                                                (12, 'REZERVABIL', 5,  1, TRUE, TRUE, FALSE, FALSE),
                                                                                                                                (12, 'REZERVABIL', 5,  2, TRUE, TRUE, FALSE, FALSE),
                                                                                                                                (12, 'REZERVABIL', 6,  1, TRUE, TRUE, FALSE, FALSE),
                                                                                                                                (12, 'REZERVABIL', 6,  2, TRUE, TRUE, FALSE, FALSE),
                                                                                                                                (12, 'REZERVABIL', 7,  1, TRUE, TRUE, FALSE, FALSE),
                                                                                                                                (12, 'REZERVABIL', 7,  2, TRUE, TRUE, FALSE, FALSE),
                                                                                                                                (12, 'REZERVABIL', 8,  1, TRUE, TRUE, FALSE, FALSE),
                                                                                                                                (12, 'REZERVABIL', 8,  2, TRUE, TRUE, FALSE, FALSE),
                                                                                                                                (12, 'REZERVABIL', 9,  1, TRUE, TRUE, FALSE, FALSE),
                                                                                                                                (12, 'REZERVABIL', 9,  2, TRUE, TRUE, FALSE, FALSE),
                                                                                                                                (12, 'REZERVABIL', 10, 1, TRUE, TRUE, FALSE, FALSE),
                                                                                                                                (12, 'REZERVABIL', 10, 2, TRUE, TRUE, FALSE, FALSE);

-- ROOM 13: 404 — Corp T2 Etaj 1
INSERT INTO seat (room_id, status, x_position, y_position, has_monitor, has_docking_station, has_standup_desk, near_window) VALUES
                                                                                                                                (13, 'REZERVABIL',         1, 1, TRUE, FALSE, FALSE, FALSE),
                                                                                                                                (13, 'NU_ESTE_REZERVABIL', 2, 1, TRUE, FALSE, FALSE, FALSE),
                                                                                                                                (13, 'REZERVABIL',         3, 1, TRUE, FALSE, FALSE, FALSE),
                                                                                                                                (13, 'REZERVABIL',         4, 1, TRUE, FALSE, FALSE, FALSE),
                                                                                                                                (13, 'NU_ESTE_REZERVABIL', 5, 1, TRUE, FALSE, FALSE, FALSE),
                                                                                                                                (13, 'REZERVABIL',         1, 2, TRUE, FALSE, FALSE, FALSE),
                                                                                                                                (13, 'REZERVABIL',         2, 2, TRUE, FALSE, FALSE, FALSE),
                                                                                                                                (13, 'NU_ESTE_REZERVABIL', 3, 2, TRUE, FALSE, FALSE, FALSE),
                                                                                                                                (13, 'REZERVABIL',         4, 2, TRUE, FALSE, FALSE, FALSE),
                                                                                                                                (13, 'REZERVABIL',         5, 2, TRUE, FALSE, FALSE, FALSE),
                                                                                                                                (13, 'REZERVABIL',         0, 3, TRUE, FALSE, FALSE, FALSE),
                                                                                                                                (13, 'REZERVABIL',         6, 3, TRUE, FALSE, FALSE, FALSE);

-- ROOM 14: Sala Birou - Etaj 2 — Corp T2 Etaj 2
INSERT INTO seat (room_id, status, x_position, y_position, has_monitor, has_docking_station, has_standup_desk, near_window) VALUES
                                                                                                                                (14, 'REZERVABIL',         1,  1, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (14, 'NU_ESTE_REZERVABIL', 2,  1, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (14, 'REZERVABIL',         3,  1, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (14, 'REZERVABIL',         4,  1, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (14, 'NU_ESTE_REZERVABIL', 5,  1, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (14, 'REZERVABIL',         6,  1, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (14, 'REZERVABIL',         7,  1, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (14, 'REZERVABIL',         8,  1, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (14, 'NU_ESTE_REZERVABIL', 9,  1, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (14, 'REZERVABIL',         10, 1, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (14, 'REZERVABIL',         1,  2, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (14, 'REZERVABIL',         2,  2, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (14, 'NU_ESTE_REZERVABIL', 3,  2, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (14, 'REZERVABIL',         4,  2, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (14, 'REZERVABIL',         5,  2, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (14, 'REZERVABIL',         6,  2, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (14, 'REZERVABIL',         7,  2, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (14, 'REZERVABIL',         8,  2, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (14, 'REZERVABIL',         9,  2, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (14, 'REZERVABIL',         10, 2, TRUE,  FALSE, FALSE, FALSE),
                                                                                                                                (14, 'REZERVABIL',         1,  3, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (14, 'REZERVABIL',         2,  3, FALSE, FALSE, FALSE, FALSE),
                                                                                                                                (14, 'REZERVABIL',         1,  4, FALSE, FALSE, FALSE, TRUE),
                                                                                                                                (14, 'REZERVABIL',         2,  4, FALSE, FALSE, FALSE, TRUE);

INSERT INTO BOOKING (
    user_id, room_id, seat_id, start_time, end_time,
    status, start_date, end_date
) VALUES
      (1, NULL, 1, '09:00', '17:00', 'CONFIRMATA', '2026-08-14', '2026-08-14'),
      (2, NULL, 3, '10:00', '18:00', 'CONFIRMATA', '2026-08-14', '2026-08-14'),
      (3, NULL, 4, '08:30', '16:30', 'IN_ASTEPTARE', '2026-08-15', '2026-08-15'),
      (4, 4, NULL, '13:00', '15:00', 'CONFIRMATA', '2026-08-16', '2026-08-16'),
      (5, 5, NULL, '11:00', '12:30', 'ANULATA', '2026-08-17', '2026-08-17');




INSERT INTO RECURRING_BOOKING (id, frequency, days_of_week, interval_of_recurrence) VALUES
                                                                                        (1, 'saptamanal', '1,3,5', 1),
                                                                                        (2, 'saptamanal', '2,4', 1),
                                                                                        (3, 'zilnic', '1,2,3,4,5', 1),
                                                                                        (4, 'saptamanal', '1', 2),
                                                                                        (5, 'saptamanal', '5', 1);


INSERT INTO NOTIFICATION (user_id, message, type, booking_id) VALUES
                                                                  (2, 'Ruxandra B. a rezervat Loc 3, Etaj 1.', 'favorite_booking', 2),
                                                                  (1, 'Rezervarea ta pentru Loc 1 a fost confirmata.', 'booking_confirmed', 1),
                                                                  (NULL, 'Ploaie prognozata maine dimineata.', 'global', NULL),
                                                                  (3, 'Ai fost invitat la birou.', 'office_invitation', NULL),
                                                                  (4, 'Rezervarea recurenta a fost activata.', 'recurring_booking', 4);


INSERT INTO USER_NOTIFICATION (user_id, has_been_read, notification_id) VALUES
                                                                            (1, FALSE, 1),
                                                                            (1, TRUE, 2),
                                                                            (2, FALSE, 3),
                                                                            (3, FALSE, 4),
                                                                            (4, TRUE, 5);

INSERT INTO USER_PREFERENCES (
    user_id, preferred_building_id, preferred_start_time,
    preferred_end_time, recieves_notification_on_email,
    near_window, quiet_place, days_of_week, reminder_before_booking,
    booking_confirmation_on_email
) VALUES
      (1, 1, '09:00', '17:00', TRUE, TRUE, TRUE, '1,2,3,4,5', TRUE, TRUE),
      (2, 1, '10:00', '18:00', TRUE, TRUE, FALSE, '1,3,5', TRUE, TRUE),
      (3, 1, '08:30', '16:30', FALSE, FALSE, TRUE, '2,4', FALSE, TRUE),
      (4, 1, '09:30', '17:30', TRUE, TRUE, TRUE, '1,2,3', TRUE, FALSE),
      (5, 1, '08:00', '16:00', TRUE, FALSE, FALSE, '1,2,3,4,5', FALSE, FALSE);