package edu.psu.se411.lab13.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import edu.psu.se411.lab13.model.Product;
import edu.psu.se411.lab13.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository products;

    public ProductService(ProductRepository products) {
        this.products = products;
    }

    public Iterable<Product> findAll() {
        return products.findAll();
    }

    public Product findById(Integer id) {
        return products.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }
}
