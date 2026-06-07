-- 카페 이름을 region과 일치하도록 수정
-- (기존: cafe_id 3 = Haeundae, 4 = Daejeon → region에 맞게 변경)
USE seathub;

UPDATE study_cafe SET cafe_name = 'StudyCafe Hyehwa' WHERE cafe_id = 3;
UPDATE study_cafe SET cafe_name = 'StudyCafe Jongno' WHERE cafe_id = 4;