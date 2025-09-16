package com.lms.lmsprojectc.web;

import com.lms.lmsprojectc.domain.Gender;
import com.lms.lmsprojectc.domain.Role;
import com.lms.lmsprojectc.domain.UserStatus;
import com.lms.lmsprojectc.entity.InstructorProfile;
import com.lms.lmsprojectc.entity.User;
import com.lms.lmsprojectc.repo.InstructorProfileRepository;
import com.lms.lmsprojectc.repo.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final InstructorProfileRepository instructorProfileRepository;

    // ───────────────────────── 회원가입 ─────────────────────────

    /** 폼 전송(x-www-form-urlencoded) */
    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> signupForm(@RequestParam MultiValueMap<String, String> params) {
        SignupForm f = new SignupForm();
        f.setUserid(params.getFirst("userid"));
        f.setEmail(params.getFirst("email"));
        f.setPassword(params.getFirst("password"));
        f.setPassword2(params.getFirst("password2"));
        f.setName(params.getFirst("name"));
        f.setBirth(params.getFirst("birth"));
        f.setGender(params.getFirst("gender"));
        f.setAddress(params.getFirst("address"));
        f.setDetailAddress(params.getFirst("detailAddress"));
        f.setPhone(params.getFirst("phone"));
        f.setRole(parseRole(params.getFirst("role"))); // "student" / "instructor"
        f.setStatus(UserStatus.active);
        // 강사 전용
        f.setAffiliation(params.getFirst("affiliation"));
        f.setIntro(params.getFirst("intro"));
        return doSignup(f);
    }

    /** JSON */
    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> signupJson(@RequestBody SignupForm f) {
        if (f.getRole() == null)   f.setRole(Role.student);
        if (f.getStatus() == null) f.setStatus(UserStatus.active);
        return doSignup(f);
    }

    /** 아이디(=닉네임) 중복 체크 */
    @GetMapping("/check-userid")
    public ResponseEntity<?> checkUserid(@RequestParam String userid) {
        String v = safe(userid);
        if (!v.matches("^[A-Za-z0-9]{4,20}$")) {
            return ResponseEntity.ok(Map.of("ok", true, "available", false, "msg", "아이디는 영문/숫자 4~20자"));
        }
        boolean exists = userRepository.findByNickname(v).isPresent();
        return ResponseEntity.ok(Map.of("ok", true, "available", !exists));
    }

    /** 실제 가입 처리 (평문 저장) */
    @Transactional
    public ResponseEntity<?> doSignup(SignupForm f) {
        // 기본 검증
        if (isBlank(f.getUserid()) || isBlank(f.getPassword()) || isBlank(f.getPassword2())
                || isBlank(f.getEmail()) || isBlank(f.getName())) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "msg", "필수값 누락"));
        }
        if (!f.getUserid().matches("^[A-Za-z0-9]{4,20}$")) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "msg", "아이디는 영문/숫자 4~20자"));
        }
        if (f.getPassword().length() < 8) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "msg", "비밀번호는 8자 이상"));
        }
        if (!f.getPassword().equals(f.getPassword2())) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "msg", "비밀번호 불일치"));
        }
        if (userRepository.findByNickname(f.getUserid()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "msg", "이미 사용 중인 아이디"));
        }
        if (userRepository.findByEmail(f.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "msg", "이미 사용 중인 이메일"));
        }

        Gender gender = "남자".equals(f.getGender()) ? Gender.male
                : "여자".equals(f.getGender()) ? Gender.female : null;
        Role role = (f.getRole() != null) ? f.getRole() : Role.student;

        LocalDate birth = null;
        if (!isBlank(f.getBirth())) {
            birth = LocalDate.parse(f.getBirth()); // yyyy-MM-dd 가정
        }

        // ⚠️ 비밀번호 평문 저장 (요청사항)
        User user = User.builder()
                .email(f.getEmail())
                .password(f.getPassword())
                .name(f.getName())
                .nickname(f.getUserid()) // 아이디 = 닉네임
                .address(join(f.getAddress(), f.getDetailAddress()))
                .phone(sanitizePhoneKeepHyphen(f.getPhone()))
                .birth_date(birth)
                .gender(gender)
                .role(role)
                .status(f.getStatus() != null ? f.getStatus() : UserStatus.active)
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();

        userRepository.save(user);

        if (role == Role.instructor) {
            InstructorProfile profile = InstructorProfile.builder()
                    .instructor(user) // @MapsId
                    .affiliation(f.getAffiliation())
                    .bio(f.getIntro())
                    .build();
            instructorProfileRepository.save(profile);
        }

        return ResponseEntity.ok(Map.of("ok", true, "userId", user.getId()));
    }

    // ───────────────────────── 로그인 ─────────────────────────

    private static final class LoginOutcome {
        final boolean ok; final int status; final String msg; final User user;
        LoginOutcome(boolean ok, int status, String msg, User user) {
            this.ok = ok; this.status = status; this.msg = msg; this.user = user;
        }
    }

    /** DB의 아이디(=nickname)와 비밀번호가 '문자 그대로' 일치하면 성공 */
    private LoginOutcome checkLogin(String nickname, String password) {
        if (isBlank(nickname) || isBlank(password)) {
            return new LoginOutcome(false, 400, "아이디/비밀번호 필요", null);
        }
        User user = userRepository.findByNickname(nickname).orElse(null);
        if (user == null) {
            return new LoginOutcome(false, 401, "존재하지 않는 아이디", null);
        }
        String stored = user.getPassword();
        if (!safe(password).equals(stored)) {
            return new LoginOutcome(false, 401, "비밀번호 불일치", null);
        }
        return new LoginOutcome(true, 200, null, user);
    }

    /** 폼 전송: 성공 시 303 → "/" */
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> loginForm(@ModelAttribute LoginForm form, HttpSession session) {
        var out = checkLogin(form.getNickname(), form.getPassword());
        if (!out.ok) return ResponseEntity.status(out.status).body(Map.of("ok", false, "msg", out.msg));

        session.setAttribute("loginUser", out.user);
        return ResponseEntity.status(303).header("Location", "/").build();
    }

    /** JSON: 성공 시 ok:true */
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loginJson(@RequestBody LoginForm form, HttpSession session) {
        var out = checkLogin(form.getNickname(), form.getPassword());
        if (!out.ok) return ResponseEntity.status(out.status).body(Map.of("ok", false, "msg", out.msg));

        session.setAttribute("loginUser", out.user);
        return ResponseEntity.ok(Map.of(
                "ok", true,
                "userId", out.user.getId(),
                "name", out.user.getName(),
                "email", out.user.getEmail(),
                "role", out.user.getRole() != null ? out.user.getRole().name() : "student"
        ));
    }

    // ───────────────────────── 세션/로그아웃 ─────────────────────────

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        User u = (User) session.getAttribute("loginUser");
        if (u == null) return ResponseEntity.ok(Map.of("ok", false));
        return ResponseEntity.ok(Map.of(
                "ok", true,
                "userId", u.getId(),
                "name", u.getName(),
                "email", u.getEmail(),
                "role", u.getRole() != null ? u.getRole().name() : "student"
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.status(303).header("Location", "/").build();
    }

    // ───────────────────────── DTO ─────────────────────────

    @lombok.Data
    public static class LoginForm {
        private String nickname;   // 닉네임(아이디)
        private String password;
    }

    @lombok.Data
    public static class SignupForm {
        private String userid;       // 닉네임으로 사용
        private String email;
        private String password;
        private String password2;
        private String name;
        private String birth;        // yyyy-MM-dd
        private String gender;       // '남자' / '여자'
        private String address;
        private String detailAddress;
        private String phone;
        // 강사 전용
        private String affiliation;
        private String intro;
        private Role role;           // student / instructor
        private UserStatus status;   // active 등
    }

    // ───────────────────────── Util ─────────────────────────

    private static boolean isBlank(String s) { return s == null || s.isBlank(); }
    private static String safe(String s) { return s == null ? "" : s.trim(); }

    private static String join(String a, String b) {
        if (isBlank(a)) return isBlank(b) ? null : b;
        return isBlank(b) ? a : a + " " + b;
    }

    private static Role parseRole(String s) {
        if (s == null) return null;
        String v = s.trim().toLowerCase();
        if ("instructor".equals(v)) return Role.instructor;
        if ("student".equals(v))    return Role.student;
        return null;
    }

    /** 숫자/하이픈만 허용, 연속/양끝 하이픈 정리 */
    private static String sanitizePhoneKeepHyphen(String s) {
        if (s == null) return null;
        String t = s.replaceAll("[^0-9-]", "");
        t = t.replaceAll("-{2,}", "-");
        t = t.replaceAll("^-|-$", "");
        return t.isEmpty() ? null : t;
    }
}