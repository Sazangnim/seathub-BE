USE seathub;

-- 기존 owner01 정보 수정
UPDATE user SET
    user_name = 'James Kim',
    email     = 'owner01@gmail.com'
WHERE user_id = 1;

-- 일반회원 10명 추가
INSERT INTO user (user_name, login_id, password, email, role, business_number, gender, age) VALUES
('Kim Minjun',   'user01', '1234', 'user01@gmail.com', 'USER', NULL, 'MALE',   22),
('Lee Seoyeon',  'user02', '1234', 'user02@gmail.com', 'USER', NULL, 'FEMALE', 25),
('Park Jiho',    'user03', '1234', 'user03@gmail.com', 'USER', NULL, 'MALE',   28),
('Choi Sua',     'user04', '1234', 'user04@gmail.com', 'USER', NULL, 'FEMALE', 23),
('Jung Daeun',   'user05', '1234', 'user05@gmail.com', 'USER', NULL, 'FEMALE', 26),
('Kang Hyunwoo', 'user06', '1234', 'user06@gmail.com', 'USER', NULL, 'MALE',   30),
('Yoon Jimin',   'user07', '1234', 'user07@gmail.com', 'USER', NULL, 'FEMALE', 21),
('Lim Dohyun',   'user08', '1234', 'user08@gmail.com', 'USER', NULL, 'MALE',   27),
('Han Sohee',    'user09', '1234', 'user09@gmail.com', 'USER', NULL, 'FEMALE', 24),
('Oh Junhyuk',   'user10', '1234', 'user10@gmail.com', 'USER', NULL, 'MALE',   29),
-- 사장 2명 추가
('Sarah Lee', 'owner02', '1234', 'owner02@gmail.com', 'OWNER', '234-56-78901', 'FEMALE', 35),
('Mike Park',  'owner03', '1234', 'owner03@gmail.com', 'OWNER', '345-67-89012', 'MALE',   45);

SELECT * FROM user;