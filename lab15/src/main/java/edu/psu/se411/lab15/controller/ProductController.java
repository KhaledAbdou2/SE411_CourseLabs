package edu.psu.se411.lab15.controller;

import java.net.URI;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import edu.psu.se411.lab15.model.Product;
import edu.psu.se411.lab15.repository.ProductRepository;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
 private final ProductRepository repository;
 private final JdbcAggregateTemplate template;
 public ProductController(ProductRepository repository, JdbcAggregateTemplate template) {
  this.repository=repository; this.template=template;
 }
 @GetMapping public Iterable<Product> all() { return repository.findAll(); }
 @GetMapping("/{id}") public Product one(@PathVariable @Positive Integer id) {
  return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
 }
 @PostMapping @Transactional
 public ResponseEntity<Product> create(@Valid @RequestBody Product body) {
  if(body.id()!=null && repository.existsById(body.id()))
   throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,"ID already exists");
  Product saved=template.insert(body);
  return ResponseEntity.created(URI.create("/api/v1/products/"+saved.id())).body(saved);
 }
 @PutMapping("/{id}") @Transactional
 public ResponseEntity<Void> update(@PathVariable @Positive Integer id, @Valid @RequestBody Product body) {
  if(body.id()!=null && !id.equals(body.id())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"ID mismatch");
  one(id);
  template.update(new Product(id, body.name(), body.description(), body.price()));
  return ResponseEntity.noContent().build();
 }
 @DeleteMapping("/{id}") @Transactional
 public ResponseEntity<Void> delete(@PathVariable @Positive Integer id) {
  one(id); repository.deleteById(id); return ResponseEntity.noContent().build();
 }
}
