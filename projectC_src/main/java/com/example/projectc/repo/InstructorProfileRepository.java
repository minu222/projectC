package com.example.projectc.repo;

import com.example.projectc.entity.InstructorProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstructorProfileRepository extends JpaRepository<InstructorProfile,Long> {
}
