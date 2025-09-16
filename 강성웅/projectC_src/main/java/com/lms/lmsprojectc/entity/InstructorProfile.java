package com.lms.lmsprojectc.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "instructor_profile")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class InstructorProfile {

    // users.user_id를 그대로 PK로 쓰는 공유키(Shared PK) 매핑
    @Id
    @Column(name = "instructor_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId                       // ← User의 PK를 이 엔티티의 PK로 ‘공유’
    @JoinColumn(name = "instructor_id")
    private User instructor;

    @Column(length = 100)
    private String affiliation;   // 소속

    @Column(columnDefinition = "TEXT")
    private String bio;           // 자기소개(= intro)
}
