package com.lms.adminpages.classrooms.entity;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MockExam {
    private Integer examId;
    private Integer instructorId;
    private Integer studentId;
    private String title;
    private String question;
    private String answer;
    private Integer score;
    private Timestamp takenAt;

    // JOIN해서 표시할 때 쓸 필드
    private String instructorName;
    private String studentName;
}
