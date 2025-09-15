package com.lms.gitprojectc.repository;

import com.lms.gitprojectc.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface InstructorRepository extends JpaRepository<User, Long> {
    // 필요한 커스텀 쿼리 작성 가능
}