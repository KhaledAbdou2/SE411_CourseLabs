INSERT INTO course (id, code, name, description, credits)
VALUES (1, 'SE201', 'Introduction to software engineering', 'This course is an intro to SE', 3);
INSERT INTO course (id, code, name, description, credits)
VALUES (2, 'CS101', 'Introduction to programming', 'This course is an intro to programming', 3);
ALTER TABLE course ALTER COLUMN id RESTART WITH 3;
