package com.lms.lmsprojectc.web;


import com.lms.lmsprojectc.entity.Post;
import com.lms.lmsprojectc.entity.User;
import com.lms.lmsprojectc.repo.PostRepository;
import com.lms.lmsprojectc.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.*;
import java.util.*;


@RestController @RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostRepository postRepository;
    private final UserRepository userRepository;


    @GetMapping
    public List<Post> list() { return postRepository.findAll(); }


    @PostMapping
    public Post create(@RequestParam Long userId, @RequestBody Post req) {
        User user = userRepository.findById(userId).orElseThrow();
        req.setUser(user);
        req.setCreated_at(LocalDateTime.now());
        req.setUpdated_at(LocalDateTime.now());
        return postRepository.save(req);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Post> get(@PathVariable Long id) {
        return postRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
