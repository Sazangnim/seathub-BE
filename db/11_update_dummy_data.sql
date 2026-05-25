USE seathub;

-- 카페 region 수정
UPDATE study_cafe SET region = 'Ewha'    WHERE cafe_id = 1;
UPDATE study_cafe SET region = 'Hongdae' WHERE cafe_id = 2;
UPDATE study_cafe SET region = 'Hyehwa'  WHERE cafe_id = 3;
UPDATE study_cafe SET region = 'Jongno'  WHERE cafe_id = 4;

-- 태그 Premium → Laptop 수정
UPDATE tag SET tag_name = 'Laptop' WHERE tag_name = 'Premium';