# User 테이블
CREATE TABLE user (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(30) UNIQUE NOT NULL,
    login_id VARCHAR(30) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(50) NOT NULL,
    role VARCHAR(10) NOT NULL,
    business_number VARCHAR(30) UNIQUE,
    status ENUM('ACTIVE','INACTIVE') DEFAULT 'ACTIVE',
    gender ENUM('MALE','FEMALE') NOT NULL,
    age INT NOT NULL
);

# StudyCafe 테이블
CREATE TABLE study_cafe (
    cafe_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    cafe_name VARCHAR(50) NOT NULL,
    region VARCHAR(30) NOT NULL,
    address VARCHAR(100) NOT NULL,

    FOREIGN KEY (user_id)
    REFERENCES user(user_id)
);

# Seat 테이블
CREATE TABLE seat (
    seat_id INT AUTO_INCREMENT PRIMARY KEY,
    cafe_id INT NOT NULL,
    seat_name VARCHAR(20) NOT NULL,
    seat_type VARCHAR(20) NOT NULL,
    status ENUM('AVAILABLE','OCCUPIED','RESERVED') NOT NULL,

    FOREIGN KEY (cafe_id)
    REFERENCES study_cafe(cafe_id)
);

# Ticket 테이블
CREATE TABLE ticket (
    ticket_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    seat_id INT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    usage_hours INT NOT NULL,

    FOREIGN KEY (user_id)
    REFERENCES user(user_id),

    FOREIGN KEY (seat_id)
    REFERENCES seat(seat_id)
);

# RoomReservation 테이블
CREATE TABLE room_reservation (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    seat_id INT NOT NULL,
    reservation_date DATE NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,

    FOREIGN KEY (user_id)
    REFERENCES user(user_id),

    FOREIGN KEY (seat_id)
    REFERENCES seat(seat_id)
);

# Tag 테이블
CREATE TABLE tag (
    tag_id INT AUTO_INCREMENT PRIMARY KEY,
    tag_name VARCHAR(30) UNIQUE NOT NULL
);

# Cafe_Tag 테이블
CREATE TABLE cafe_tag (
    cafe_id INT NOT NULL,
    tag_id INT NOT NULL,

    FOREIGN KEY (cafe_id)
    REFERENCES study_cafe(cafe_id),

    FOREIGN KEY (tag_id)
    REFERENCES tag(tag_id)
);