package com.lms.lmsprojectc.repo;

import com.lms.lmsprojectc.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long> {
}
