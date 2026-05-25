USE seathub;

-- tag_id: 1=24H / 2=Women Only / 3=Student Only / 4=Quiet / 5=Premium
INSERT INTO tag (tag_name) VALUES
('24H'),
('Women Only'),
('Student Only'),
('Quiet'),
('Premium');

INSERT INTO cafe_tag (cafe_id, tag_id) VALUES
(1, 1), -- Ewha    - 24H
(1, 2), -- Ewha    - Women Only
(1, 4), -- Ewha    - Quiet
(2, 1), -- Hongdae - 24H
(2, 5), -- Hongdae - Premium
(3, 3), -- Hyehwa  - Student Only
(3, 4), -- Hyehwa  - Quiet
(4, 5), -- Jongno  - Premium
(4, 1); -- Jongno  - 24H

