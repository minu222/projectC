package com.lms.projectc.users.service;

import com.lms.projectc.users.entity.User;
import com.lms.projectc.users.repository.UserRepository;
import com.lms.projectc.users.repository.InstructorProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final InstructorProfileRepository instructorProfileRepo;

    public UserService(UserRepository userRepo,
                       InstructorProfileRepository instructorProfileRepo) {
        this.userRepo = userRepo;
        this.instructorProfileRepo = instructorProfileRepo;
    }

    /**
     * 기존 컨트롤러 호환용: 강사 추가정보 없이 가입
     * (instructor_profile 저장 없음)
     */
    public int join(User user) {
        // 중복 체크
        validateDuplicateUser(user);

        // users 저장 (+생성된 PK를 User에 세팅)
        userRepo.saveAndReturnId(user);

        // 필요 시: 강사면 instructor_profile도 저장하고 싶다면
        // joinWithInstructorProfile(user, null, null) 로 대체하세요.
        return user.getUser_id();
    }

    /**
     * 강사 추가정보(소속/소개)까지 함께 저장하는 가입
     * - users INSERT 후 생성된 user_id로 instructor_profile upsert
     * - 동일 트랜잭션: 하나라도 실패하면 전체 롤백
     */
    @Transactional
    public long joinWithInstructorProfile(User user, String affiliation, String bio) {
        validateDuplicateUser(user);

        long newUserId = userRepo.saveAndReturnId(user);

        if (user.getRole() == User.Role.INSTRUCTOR) {
            instructorProfileRepo.upsert(newUserId, trimToNull(affiliation), trimToNull(bio));
        }
        return newUserId;
    }

    private void validateDuplicateUser(User user) {
        userRepo.findByUsername(user.getNickname())
                .ifPresent(u -> { throw new IllegalArgumentException("이미 존재하는 아이디 입니다."); });
        // 필요하면 email 중복도 여기서 체크하세요 (repo에 findByEmail 추가)
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    // ===== CRUD / 조회 =====

    public void register(User user) {
        userRepo.add(user); // 내부적으로 save 호출
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public Optional<User> findById(int id) {
        return userRepo.findById(id);
    }

    // 컨트롤러가 String id를 넘기는 경우를 위한 헬퍼
    public Optional<User> findById(String id) {
        try {
            return userRepo.findById(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public void update(User user) {
        userRepo.update(user);
    }

    public void deleteById(int id) {
        userRepo.deleteById(id);
    }

    public void deleteById(String id) {
        try {
            userRepo.deleteById(Integer.parseInt(id));
        } catch (NumberFormatException ignored) { }
    }

    // ===== 로그인(보안 미적용, 평문 비교) =====

    public Optional<User> login(String nickname, String password) {
        return userRepo.findByUsername(nickname)
                .filter(u -> u.getPassword().equals(password));
    }
}
