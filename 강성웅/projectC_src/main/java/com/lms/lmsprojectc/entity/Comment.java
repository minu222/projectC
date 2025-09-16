package com.lms.lmsprojectc.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.*;


@Entity @Table(name="comments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Comment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parent;


    @Lob private String content;
    private Integer likes = 0;
    private boolean is_deleted = false;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
