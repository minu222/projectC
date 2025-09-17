package com.lms.mainpages.users.service;

import com.lms.mainpages.users.entity.InstructorProfile;
import com.lms.mainpages.users.entity.User;
import com.lms.mainpages.users.repository.InstructorProfileRepository;
import com.lms.mainpages.users.repository.UserRepository;
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
        // 저장 후 생성된 PK 반환 (saveAndReturnId 내부에서 user.setUser_id(...)까지 해두면 더 좋음)
        long id = userRepo.saveAndReturnId(user);
        return (int) id;
    }

    /** 강사 추가정보까지 함께 저장 */
    @Transactional
    public long joinWithInstructorProfile(User user, String affiliation, String bio) {
        validateDuplicateUser(user);

        long newUserId = userRepo.saveAndReturnId(user);

        if (user.getRole() != null && user.getRole() == User.Role.INSTRUCTOR) {
            instructorRepo.upsert(newUserId, trimToNull(affiliation), trimToNull(bio));
        }
        return newUserId;
    }

    /* ==================== 프로필 수정 ==================== */

    /**
     * 프로필 수정
     * - users.email / users.phone / users.address 갱신
     * - 강사면 instructor_profile (affiliation, bio) upsert
     */
    @Transactional
    public void updateProfile(User formUser, String affiliation, String bio) {
        User current = userRepo.findById(formUser.getUser_id())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 닉네임이 바뀌는 경우 중복 체크
        if (!Objects.equals(current.getNickname(), formUser.getNickname())) {
            if (userRepo.existsByNicknameExceptId(formUser.getNickname(), current.getUser_id())) {
                throw new IllegalArgumentException("이미 사용 중인 아이디(닉네임)입니다.");
            }
        }

        // 비밀번호는 빈 값이면 변경하지 않음
        String pwOrNull = (formUser.getPassword() == null || formUser.getPassword().isBlank())
                ? null
                : formUser.getPassword(); // 실제 운영에선 BCrypt 등으로 해시!

        int rows = userRepo.updateProfileAllFields(
                current.getUser_id(),
                formUser.getNickname(),
                formUser.getName(),
                formUser.getEmail(),
                formUser.getPhone(),
                formUser.getAddress(),
                pwOrNull
        );
        if (rows == 0) throw new IllegalStateException("프로필 갱신에 실패했습니다.");

        // 강사 추가 정보 업서트
        if (current.getRole() != null &&
                "INSTRUCTOR".equalsIgnoreCase(current.getRole().toString())) {
            instructorRepo.upsert(current.getUser_id(),
                    trimToNull(affiliation),
                    trimToNull(bio));
        }
    }

    /* ==================== 조회/CRUD ==================== */

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public Optional<User> findById(int id) {
        return userRepo.findById(id);
    }

    // 컨트롤러에서 String id를 넘기는 경우 대비
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
        // 필요 시 이메일 중복검사 추가
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}