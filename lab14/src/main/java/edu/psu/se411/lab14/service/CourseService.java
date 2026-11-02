package edu.psu.se411.lab14.service;

import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import edu.psu.se411.lab14.model.Course;
import edu.psu.se411.lab14.repository.CourseRepository;

@Service
public class CourseService {
    private final CourseRepository courses;
    private final JdbcAggregateTemplate template;

    public CourseService(CourseRepository courses, JdbcAggregateTemplate template) {
        this.courses = courses;
        this.template = template;
    }

    public Iterable<Course> getAllCourses() {
        return courses.findAll();
    }

    public Course getCourseById(Long id) {
        return courses.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
    }

    @Transactional
    public Course create(Course course) {
        if (courses.existsById(course.id())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Course ID already exists");
        }
        // save() treats a supplied ID as an update. insert() explicitly creates it.
        return template.insert(course);
    }

    @Transactional
    public void update(Long id, Course course) {
        if (!id.equals(course.id())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Path and body IDs must match");
        }
        getCourseById(id);
        template.update(course);
    }

    @Transactional
    public void delete(Long id) {
        getCourseById(id);
        courses.deleteById(id);
    }
}
