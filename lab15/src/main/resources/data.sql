INSERT INTO product(name,description,price) SELECT '3d printer','A beginner friendly 3d printer',1840.25 WHERE NOT EXISTS (SELECT 1 FROM product);
