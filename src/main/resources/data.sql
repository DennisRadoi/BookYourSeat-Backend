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



INSERT INTO BUILDING (name, address_id) VALUES
                                            ('ITSS T1', 1),
                                            ('ITSS T2', 2),
                                            ('Corp Training', 1),
                                            ('Corp Administrativ', 2),
                                            ('Corp Meeting', 1);


INSERT INTO ROOM (floor, building_id, name, type) VALUES
                                                      (0, 1, 'Open Space Parter T1', 'DE_OFICIU'),
                                                      (1, 1, 'Open Space Etaj 1 T1', 'DE_OFICIU'),
                                                      (2, 2, 'Open Space Etaj 2 T2', 'DE_OFICIU'),
                                                      (1, 2, 'Sala Conferinta T2', 'DE_CONFERINTA'),
                                                      (0, 5, 'Sala Meeting Parter', 'DE_CONFERINTA');


INSERT INTO SEAT (
    room_id, status, x_position, y_position,
    has_monitor, has_docking_station, has_standup_desk, near_window
) VALUES
      (1, 'REZERVABIL', 1, 1, TRUE, TRUE, FALSE, TRUE),
      (1, 'REZERVABIL', 2, 1, FALSE, TRUE, FALSE, FALSE),
      (2, 'REZERVABIL', 1, 2, TRUE, TRUE, FALSE, TRUE),
      (3, 'REZERVABIL', 2, 2, TRUE, TRUE, TRUE, FALSE),
      (3, 'NU_ESTE_REZERVABIL', 3, 2, FALSE, FALSE, FALSE, TRUE);


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