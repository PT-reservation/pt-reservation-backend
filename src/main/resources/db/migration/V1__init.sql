CREATE TABLE member (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        email VARCHAR(255) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL,
                        name VARCHAR(50) NOT NULL,
                        role VARCHAR(20) NOT NULL,
                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE fitness_class (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               trainer_id BIGINT NOT NULL,
                               title VARCHAR(100) NOT NULL,
                               class_date_time DATETIME NOT NULL,
                               capacity INT NOT NULL,
                               current_count INT NOT NULL DEFAULT 0,
                               version BIGINT NOT NULL DEFAULT 0,
                               created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (trainer_id) REFERENCES member(id)
);

CREATE TABLE reservation (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             class_id BIGINT NOT NULL,
                             member_id BIGINT NOT NULL,
                             status VARCHAR(20) NOT NULL,
                             reserved_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             cancelled_at DATETIME NULL,
                             FOREIGN KEY (class_id) REFERENCES fitness_class(id),
                             FOREIGN KEY (member_id) REFERENCES member(id)
);

CREATE TABLE session_ticket (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                member_id BIGINT NOT NULL,
                                total_count INT NOT NULL,
                                remaining_count INT NOT NULL,
                                issued_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                FOREIGN KEY (member_id) REFERENCES member(id)
);