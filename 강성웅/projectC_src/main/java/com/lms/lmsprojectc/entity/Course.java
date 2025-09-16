package com.lms.lmsprojectc.entity;


import com.lms.lmsprojectc.domain.CourseStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;


@Entity @Table(name="courses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Course {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    private InstructorProfile instructor;


    private String title;
    @Lob private String description;
    private String category;


    @Column(precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;
    private boolean is_free;


    @Column(precision = 3, scale = 2)
    private BigDecimal avg_rating = BigDecimal.ZERO;


    @Enumerated(EnumType.STRING)
    private CourseStatus status = CourseStatus.draft;


    private Integer student_count = 0;
    private LocalDate expiry_date;
    private Integer live_limit = 20;


    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime deleted_at;
}
