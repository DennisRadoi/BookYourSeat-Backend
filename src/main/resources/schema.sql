CREATE TABLE IF NOT EXISTS DEPARTMENT(
                                         id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                         name VARCHAR(255) NOT NULL,
                                         description VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS COUNTY(
                                     id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                     name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS LOCALITY(
                                       id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                       name VARCHAR(255) NOT NULL,
                                       county_id INT REFERENCES COUNTY(id)
);

CREATE TABLE IF NOT EXISTS ADDRESS(
                                      id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                      street VARCHAR(255) NOT NULL,
                                      number VARCHAR(255) NOT NULL,
                                      floor INT,
                                      apartment_block VARCHAR(255),
                                      postal_code VARCHAR(6) NOT NULL,
                                      locality_id INT REFERENCES LOCALITY(id),
                                      type VARCHAR(255) NOT NULL CHECK(
                                          type IN('DE_DOMICILIU', 'DE_OFICIU')
                                          )
);


CREATE TABLE IF NOT EXISTS USERS(
                                    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                    last_name VARCHAR(255) NOT NULL,
                                    first_name VARCHAR(255) NOT NULL,
                                    email VARCHAR(255) NOT NULL UNIQUE,
                                    department_id INT REFERENCES DEPARTMENT(id),
                                    role VARCHAR(255),
                                    phone_number VARCHAR(10) UNIQUE,
                                    address_id INT REFERENCES ADDRESS(id),
                                    profile_photo VARCHAR(255),
                                    employment_date DATE,
                                    is_active BOOLEAN DEFAULT TRUE,
                                    password_hash VARCHAR(255) NOT NULL,
                                    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS FAVORITE_COLLEAGUE(
                                                 id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                                 user_id INT REFERENCES USERS(id),
                                                 favorite_colleague_id INT REFERENCES USERS(id),
                                                 created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                                 CONSTRAINT chk_favorite CHECK(user_id <> favorite_colleague_id)
);

CREATE TABLE IF NOT EXISTS OFFICE_INVITATION(
                                                id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                                user_id INT REFERENCES USERS(id),
                                                addressee_id INT REFERENCES USERS(id),
                                                message VARCHAR(255) NOT NULL,
                                                created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                                answered_at TIMESTAMPTZ,
                                                proposed_date DATE, -- nou
                                                CONSTRAINT chk_office_invite CHECK(user_id <> addressee_id)
);

CREATE TABLE IF NOT EXISTS USER_PREFERENCES(
                    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    user_id INT REFERENCES USERS(id),
                                               preferred_floor VARCHAR(255),
                                               preferred_seat_type VARCHAR(255),
                                               preferred_start_time TIME,
                                               preferred_end_time TIME,
                                               recieves_notification_on_email BOOLEAN DEFAULT TRUE,
                                               preferred_building VARCHAR(255),
                                               near_window BOOLEAN NOT NULL,
                                               quiet_place BOOLEAN NOT NULL,
                                               days_of_week VARCHAR(255),
                                                reminder_before_booking BOOLEAN NOT NULL,
                                                booking_confirmation_on_email BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS BUILDING(
                                       id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                       name VARCHAR(255) NOT NULL,
                                       address_id INT REFERENCES ADDRESS(id)
);

CREATE TABLE IF NOT EXISTS ROOM(
                                   id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                   floor INT NOT NULL,
    --capacity INT NOT NULL dar se poate calcula cu SELECT COUNT din SEAT
                                   building_id INT REFERENCES BUILDING(id),
                                   name VARCHAR(255) NOT NULL,
                                   type VARCHAR(255) NOT NULL CHECK(
                                       type IN('DE_OFICIU', 'DE_CONFERINTA')
                                       )
);

CREATE TABLE IF NOT EXISTS SEAT(
                                   id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                   room_id INT REFERENCES ROOM(id),
                                   status VARCHAR(255) NOT NULL CHECK(
                                       status IN('REZERVABIL', 'NU_ESTE_REZERVABIL') -- nu neaparat tine de ocupat
                                       ),
                                   x_position INT NOT NULL,
                                   y_position INT NOT NULL,
                                   has_monitor BOOLEAN NOT NULL,
                                   has_docking_station BOOLEAN NOT NULL,
                                   has_standup_desk BOOLEAN NOT NULL,
                                   near_window BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS BOOKING(
                                      id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                      user_id INT NOT NULL REFERENCES USERS(id),
                                      room_id INT REFERENCES ROOM(id),
                                      seat_id INT REFERENCES SEAT(id),
                                      start_time TIME NOT NULL,
                                      end_time TIME NOT NULL,
                                      status VARCHAR(255) NOT NULL CHECK(
                                          status IN('IN_ASTEPTARE', 'FINALIZATA', 'ANULATA', 'CONFIRMATA')
                                          ),
                                      created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                      updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                      start_date DATE NOT NULL,
                                      end_date DATE NOT NULL,
                                      CONSTRAINT chk_booking CHECK(
                                          (room_id IS NULL AND seat_id IS NOT NULL)
                                              OR
                                          (room_id IS NOT NULL AND seat_id IS NULL)
                                          ),
                                      CONSTRAINT chk_booking_interval CHECK(
                                          start_time < end_time
                                          )
);

CREATE TABLE IF NOT EXISTS RECURRING_BOOKING(
                                                id INT PRIMARY KEY REFERENCES BOOKING(id),
                                                frequency VARCHAR(255) NOT NULL,
                                                days_of_week VARCHAR(16) NOT NULL,
                                                interval_of_recurrence INT NOT NULL
);

CREATE TABLE IF NOT EXISTS NOTIFICATION(
                                           id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                           user_id INT REFERENCES USERS(id), -- id ul celui ce apare in notificare
                                           message VARCHAR(255),
                                           type VARCHAR(255),
                                           created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                           booking_id INT REFERENCES BOOKING(id)
);

CREATE TABLE IF NOT EXISTS USER_NOTIFICATION(
                                                id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                                user_id INT REFERENCES USERS(id),
                                                has_been_read BOOLEAN,
                                                notification_id INT REFERENCES NOTIFICATION(id)
);