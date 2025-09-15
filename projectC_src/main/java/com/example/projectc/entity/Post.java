package com.example.projectc.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.*;


@Entity @Table(name="posts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Post {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    private String title;
    @Lob private String content;
    private String category;
    private Integer views = 0;
    private Integer likes = 0;
    private Integer comments_count = 0;
    private boolean is_deleted = false;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
