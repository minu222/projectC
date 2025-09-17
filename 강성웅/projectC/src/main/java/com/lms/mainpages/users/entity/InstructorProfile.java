package com.lms.mainpages.users.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstructorProfile {
    private int instructorId;   // DB 컬럼 instructor_id 매핑
    private String affiliation;
    private String bio;
    // 필요시 createdAt/updatedAt 추가
}
