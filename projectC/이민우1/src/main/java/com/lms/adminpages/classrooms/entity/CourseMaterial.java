package com.lms.adminpages.classrooms.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseMaterial {
    private Integer materialId;
    private Integer courseId;
    private String name;
    private String filePath;
    private String fileType;
    private Boolean hasExam;
    private Boolean hasReplay;
    private String courseTitle;
}