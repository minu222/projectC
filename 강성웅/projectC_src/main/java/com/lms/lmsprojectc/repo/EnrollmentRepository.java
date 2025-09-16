package com.lms.lmsprojectc.repo;

import com.lms.lmsprojectc.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment,Long> {
}
