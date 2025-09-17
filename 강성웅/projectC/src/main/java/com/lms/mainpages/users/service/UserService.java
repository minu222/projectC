package com.lms.mainpages.users.service;

import com.lms.mainpages.users.entity.InstructorProfile;
import com.lms.mainpages.users.entity.User;
import com.lms.mainpages.users.repository.UserRepository;
import com.lms.mainpages.users.repository.InstructorProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final InstructorProfileRepository instructorRepo;

    public UserService(UserRepository userRepo,
                       InstructorProfileRepository instructorRepo) {
        this.userRepo = userRepo;
        this.instructorRepo = instructorRepo;
    }

    /* ==================== 가입 ==================== */

    /** 기본 가입 (강사 추가정보 저장 안 함) */
    public int join(User user) {
        validateDuplicateUser(user);
        // saveAndReturnId는 저장 후 생성된 PK를 반환하고 user.setUser_id(...)까지 수행하게 구현하세요.
        return (int) userRepo.saveAndReturnId(user);
    }

    /** 강사 추가정보까지 함께 저장 */
    @Transactional
    public long joinWithInstructorProfile(User user, String affiliation, String bio) {
        validateDuplicateUser(user);

        long userId = userRepo.saveAndReturnId(user);

        if (user.getRole() != null && user.getRole() == User.Role.INSTRUCTOR) {
            instructorRepo.upsert(userId, trimToNull(affiliation), trimToNull(bio));
        }
        return userId;
    }

    /* ==================== 프로필 수정 ==================== */

    /**
     * 프로필 수정
     * - users.email/phone/address 갱신
     * - 강사면 instructor_profile upsert
     */
    @Transactional
    public void updateProfile(User formUser, String affiliation, String bio) {
        User current = userRepo.findById(formUser.getUser_id())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // users 테이블 갱신 (UserRepository에 구현)
        int rows = userRepo.updateProfileFields(
                current.getUser_id(),
                formUser.getEmail(),
                formUser.getPhone(),
                formUser.getAddress()
        );
        if (rows == 0) {
            throw new IllegalStateException("프로필 갱신에 실패했습니다.");
        }

        // 강사 프로필 upsert
        if (current.getRole() != null && current.getRole() == User.Role.INSTRUCTOR) {
            instructorRepo.upsert(current.getUser_id(), trimToNull(affiliation), trimToNull(bio));
        }
    }

    /* ==================== 조회/CRUD ==================== */

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public Optional<User> findById(int id) {
        return userRepo.findById(id);
    }

    // 컨트롤러에서 String id를 줄 때 대비
    public Optional<User> findById(String id) {
        try {
            return userRepo.findById(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public void deleteById(int id) {
        userRepo.deleteById(id);
    }

    public void deleteById(String id) {
        try {
            userRepo.deleteById(Integer.parseInt(id));
        } catch (NumberFormatException ignored) { }
    }

    /* ==================== 로그인 (예시) ==================== */

    public Optional<User> login(String nickname, String password) {
        return userRepo.findByUsername(nickname)
                .filter(u -> Objects.equals(u.getPassword(), password));
    }

    /* ==================== 강사 프로필 조회 ==================== */

    public Optional<InstructorProfile> findInstructorProfile(int userId) {
        return instructorRepo.findByUserId(userId);
    }

    /* ==================== 유틸 ==================== */

    private void validateDuplicateUser(User user) {
        userRepo.findByUsername(user.getNickname())
                .ifPresent(u -> { throw new IllegalArgumentException("이미 존재하는 아이디 입니다."); });
        // 필요하면 email 중복도 추가 검사
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    public void update(User user) {
    }
}