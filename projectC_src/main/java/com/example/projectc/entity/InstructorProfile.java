package com.example.projectc.entity;


import jakarta.persistence.*;
import lombok.*;


@Entity @Table(name = "instructor_profile")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InstructorProfile {
    @Id
    @Column(name = "instructor_id")
    private Long id; // users.user_id 와 동일 키


    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "instructor_id")
    private User user;


    private String affiliation;
    @Lob
    private String bio;
}
