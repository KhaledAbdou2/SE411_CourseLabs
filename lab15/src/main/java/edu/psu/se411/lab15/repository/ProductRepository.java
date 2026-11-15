package edu.psu.se411.lab15.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import edu.psu.se411.lab15.model.Product;
@Repository
public interface ProductRepository extends CrudRepository<Product,Integer> {}
