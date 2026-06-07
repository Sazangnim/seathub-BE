# 자식 테이블 외래키 제약조건 설정

# == seat - cafe_id ==
# SHOW CREATE TABLE seat;

ALTER TABLE seat DROP FOREIGN KEY seat_ibfk_1;

ALTER TABLE seat 
ADD CONSTRAINT fk_seat_cafe
FOREIGN KEY (cafe_id)
REFERENCES study_cafe(cafe_id)
ON DELETE CASCADE;

# == ticket - user_id, seat_id ==
# SHOW CREATE TABLE ticket;

ALTER TABLE ticket DROP FOREIGN KEY ticket_ibfk_1;

ALTER TABLE ticket
ADD CONSTRAINT fk_ticket_user
FOREIGN KEY (user_id)
REFERENCES user(user_id)
ON DELETE CASCADE;

ALTER TABLE ticket DROP FOREIGN KEY ticket_ibfk_2;

ALTER TABLE ticket
ADD CONSTRAINT fk_ticket_seat
FOREIGN KEY (seat_id)
REFERENCES seat(seat_id)
ON DELETE CASCADE;

# == room_reservation - user_id, seat_id ==
# SHOW CREATE TABLE room_reservation;

ALTER TABLE room_reservation DROP FOREIGN KEY room_reservation_ibfk_1;

ALTER TABLE room_reservation
ADD CONSTRAINT fk_room_reservation_user
FOREIGN KEY (user_id)
REFERENCES user(user_id)
ON DELETE CASCADE;

ALTER TABLE room_reservation DROP FOREIGN KEY room_reservation_ibfk_2;

ALTER TABLE room_reservation
ADD CONSTRAINT fk_room_reservation_seat
FOREIGN KEY (seat_id)
REFERENCES seat(seat_id)
ON DELETE CASCADE;

# == cafe_tag - cafe_id, tag_id ==
# SHOW CREATE TABLE cafe_tag;

ALTER TABLE cafe_tag DROP FOREIGN KEY cafe_tag_ibfk_1;

ALTER TABLE cafe_tag
ADD CONSTRAINT fk_cafe_tag_cafe
FOREIGN KEY (cafe_id)
REFERENCES study_cafe(cafe_id)
ON DELETE CASCADE;

ALTER TABLE cafe_tag DROP FOREIGN KEY cafe_tag_ibfk_2;

ALTER TABLE cafe_tag
ADD CONSTRAINT fk_cafe_tag_tag
FOREIGN KEY (tag_id)
REFERENCES tag(tag_id)
ON DELETE CASCADE;