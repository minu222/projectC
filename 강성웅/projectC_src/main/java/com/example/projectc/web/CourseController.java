package com.example.projectc.web;


import com.example.projectc.entity.Course;
import com.example.projectc.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;


@RestController @RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;


    @GetMapping
    public List<Course> list() { return courseService.list(); }


    @GetMapping("/{id}")
    public ResponseEntity<Course> get(@PathVariable Long id) {
        return courseService.get(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
