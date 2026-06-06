USE seathub;

-- 지역별 카페 조회용 인덱스 추가
CREATE INDEX idx_study_cafe_region ON study_cafe(region);

-- 태그별 카페 조회용 인덱스 추가
CREATE INDEX idx_cafe_tag_cafe ON cafe_tag(cafe_id);
CREATE INDEX idx_cafe_tag_tag  ON cafe_tag(tag_id);