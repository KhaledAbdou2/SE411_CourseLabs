package edu.psu.se411.lab16.controller;

import java.net.URI;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import edu.psu.se411.lab16.model.Course;
import edu.psu.se411.lab16.repository.CourseRepository;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {
 private final CourseRepository repository;
 private final JdbcAggregateTemplate template;
 public CourseController(CourseRepository repository, JdbcAggregateTemplate template) {
  this.repository=repository; this.template=template;
 }
 @GetMapping public Iterable<Course> all() { return repository.findAll(); }
 @GetMapping("/{id}") public Course one(@PathVariable @Positive Integer id) {
  return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
 }
 @PostMapping @Transactional
 public ResponseEntity<Course> create(@Valid @RequestBody Course body) {
  if(body.id()!=null && repository.existsById(body.id()))
   throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,"ID already exists");
  Course saved=template.insert(body);
  return ResponseEntity.created(URI.create("/api/v1/courses/"+saved.id())).body(saved);
 }
 @PutMapping("/{id}") @Transactional
 public ResponseEntity<Void> update(@PathVariable @Positive Integer id, @Valid @RequestBody Course body) {
  if(body.id()!=null && !id.equals(body.id())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"ID mismatch");
  one(id);
  template.update(new Course(id, body.code(), body.name(), body.description(), body.credits()));
  return ResponseEntity.noContent().build();
 }
 @DeleteMapping("/{id}") @Transactional
 public ResponseEntity<Void> delete(@PathVariable @Positive Integer id) {
  one(id); repository.deleteById(id); return ResponseEntity.noContent().build();
 }
}
