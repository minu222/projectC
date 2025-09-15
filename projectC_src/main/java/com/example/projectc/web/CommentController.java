package com.example.projectc.web;


import com.example.projectc.entity.*;
import com.example.projectc.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.*;


@RestController @RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;


    @PostMapping
    public ResponseEntity<Comment> add(@PathVariable Long postId,
                                       @RequestParam Long userId,
                                       @RequestBody Comment req) {
        Post post = postRepository.findById(postId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        req.setPost(post);
        req.setUser(user);
        req.setCreated_at(LocalDateTime.now());
        req.setUpdated_at(LocalDateTime.now());
        return ResponseEntity.ok(commentRepository.save(req));
    }
}
