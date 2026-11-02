package edu.psu.se411.lab14.controller;

import java.net.URI;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import edu.psu.se411.lab14.model.Course;
import edu.psu.se411.lab14.service.CourseService;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {
    private final CourseService courses;

    public CourseController(CourseService courses) {
        this.courses = courses;
    }

    @GetMapping
    public ResponseEntity<Iterable<Course>> getAllCourses() {
        return ResponseEntity.ok(courses.getAllCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable @PositiveOrZero Long id) {
        return ResponseEntity.ok(courses.getCourseById(id));
    }

    @PostMapping
    public ResponseEntity<Course> create(@Valid @RequestBody Course course) {
        Course created = courses.create(course);
        return ResponseEntity.created(URI.create("/api/v1/courses/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable @PositiveOrZero Long id,
                                       @Valid @RequestBody Course course) {
        courses.update(id, course);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @PositiveOrZero Long id) {
        courses.delete(id);
        return ResponseEntity.noContent().build();
    }
}
