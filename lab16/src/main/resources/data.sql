INSERT INTO course(code,name,description,credits) SELECT 'SE411','Software Construction','Spring security lab',3 WHERE NOT EXISTS (SELECT 1 FROM course);
