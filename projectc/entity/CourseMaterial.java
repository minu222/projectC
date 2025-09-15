package com.example.projectc.entity;


import jakarta.persistence.*;
import lombok.*;


@Entity @Table(name="course_materials")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CourseMaterial {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "material_id")
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;


    private String name;
    private String file_path;
    private String file_type;
    private boolean has_exam;
    private boolean has_replay = true;
}
