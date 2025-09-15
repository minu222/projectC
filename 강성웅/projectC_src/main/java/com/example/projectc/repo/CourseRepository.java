package com.example.projectc.repo;
import com.example.projectc.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CourseRepository extends JpaRepository<Course, Long> { }
