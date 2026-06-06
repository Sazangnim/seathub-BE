-- 1. status 컬럼 삭제
ALTER TABLE user DROP COLUMN status;

-- 2. role 컬럼을 ENUM으로 변경 
ALTER TABLE user MODIFY COLUMN role ENUM('USER', 'OWNER') NOT NULL;