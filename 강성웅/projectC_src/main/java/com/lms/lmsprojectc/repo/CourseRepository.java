package com.lms.lmsprojectc.repo;
import com.lms.lmsprojectc.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CourseRepository extends JpaRepository<Course, Long> { }
