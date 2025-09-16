package com.lms.lmsprojectc.service;


import com.lms.lmsprojectc.entity.Course;
import com.lms.lmsprojectc.repo.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;


@Service @RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    public List<Course> list() { return courseRepository.findAll(); }
    public Optional<Course> get(Long id) { return courseRepository.findById(id); }
}
