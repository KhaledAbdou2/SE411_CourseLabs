package edu.psu.se411.lab16.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import edu.psu.se411.lab16.model.Course;
@Repository
public interface CourseRepository extends CrudRepository<Course,Integer> {}
