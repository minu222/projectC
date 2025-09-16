package com.lms.lmsprojectc.web;


import com.lms.lmsprojectc.entity.*;
import com.lms.lmsprojectc.repo.*;
import com.lms.lmsprojectc.entity.Comment;
import com.lms.lmsprojectc.entity.Post;
import com.lms.lmsprojectc.entity.User;
import com.lms.lmsprojectc.repo.CommentRepository;
import com.lms.lmsprojectc.repo.PostRepository;
import com.lms.lmsprojectc.repo.UserRepository;
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
