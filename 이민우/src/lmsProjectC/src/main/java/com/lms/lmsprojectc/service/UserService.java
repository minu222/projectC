package com.lms.lmsprojectc.service;

import com.lms.lmsprojectc.domain.Role;
import com.lms.lmsprojectc.domain.UserStatus;
import com.lms.lmsprojectc.entity.User;
import com.lms.lmsprojectc.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    //--------------------------강사 정보-----------------------
    // 검색 + 상태 필터
    public List<User> getAllInstructors(String department, String type, String keyword, String status) {
        List<User> list = userRepository.findByRole(Role.instructor);

        return list.stream()
                .filter(u -> {
                    if (type == null || keyword == null || keyword.isEmpty()) return true;
                    if (type.equals("nickname")) return u.getNickname().contains(keyword);
                    if (type.equals("name")) return u.getName().contains(keyword);
                    return true;
                })
                .filter(u -> {
                    if (status == null || status.equals("all")) return true;
                    return u.getStatus().name().equalsIgnoreCase(status);
                })
                .toList();
    }

    // 인자 없는 호출용
    public List<User> getAllInstructors() {
        return getAllInstructors(null, null, null, "all");
    }

    // 선택 탈퇴
    @Transactional
    public void deleteInstructors(List<Integer> ids) {
        List<User> instructors = userRepository.findAllById(ids);
        for (User u : instructors) {
            u.setStatus(UserStatus.deleted);
        }
        userRepository.saveAll(instructors);
    }
    //------------------------------------------------


    //-----------------------------학생 정보----------------------
    // 학생 조회
    public List<User> getAllStudents(String type, String keyword, String status) {
        List<User> list = userRepository.findByRole(Role.student);

        return list.stream()
                .filter(u -> {
                    if (type == null || keyword == null || keyword.isEmpty()) return true;
                    if (type.equals("nickname")) return u.getNickname().contains(keyword);
                    if (type.equals("name")) return u.getName().contains(keyword);
                    return true;
                })
                .filter(u -> {
                    if (status == null || status.equals("all")) return true;
                    return u.getStatus().name().equalsIgnoreCase(status);
                })
                .toList();
    }

    // 선택 탈퇴
    @Transactional
    public void deleteStudents(List<Integer> ids) {
        List<User> students = userRepository.findAllById(ids);
        for (User u : students) {
            u.setStatus(UserStatus.deleted);
        }
        userRepository.saveAll(students);
    }
    //-------------------------------------------------------
}

