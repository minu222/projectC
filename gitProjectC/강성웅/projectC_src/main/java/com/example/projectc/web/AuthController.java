package com.example.projectc.web;

import com.example.projectc.domain.Gender;
import com.example.projectc.domain.Role;
import com.example.projectc.domain.UserStatus;
import com.example.projectc.entity.User;
import com.example.projectc.repo.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    // =========================
    // 아이디(닉네임) 중복 체크
    // =========================
    @GetMapping("/check-userid")
    public ResponseEntity<?> checkUserId(@RequestParam("userid") String userid) {
        String uid = userid == null ? null : userid.trim();
        if (isBlank(uid)) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "msg", "아이디를 입력하세요."));
        }
        // ▼ 밑줄 제거
        if (!uid.matches("^[A-Za-z0-9]{4,20}$")) {
            return ResponseEntity.ok(Map.of("ok", true, "available", false, "msg", "아이디는 영문/숫자 4~20자"));
        }
        boolean exists = userRepository.existsByNickname(uid);
        return ResponseEntity.ok(Map.of("ok", true, "available", !exists));
    }

    // =========================
    // 회원가입 (폼: x-www-form-urlencoded)
    // =========================
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
        f.setNoteAgree("on".equalsIgnoreCase(params.getFirst("noteAgree")));
        f.setEmailAgree("on".equalsIgnoreCase(params.getFirst("emailAgree")));
        // 기본값 보장
        f.setRole(Role.student);
        f.setStatus(UserStatus.active);

        return doSignup(f);
    }

    // =========================
    // 회원가입 (JSON)
    // =========================
    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> signupJson(@RequestBody SignupForm form) {
        if (form.getRole() == null)   form.setRole(Role.student);
        if (form.getStatus() == null) form.setStatus(UserStatus.active);
        return doSignup(form);
    }

    private ResponseEntity<?> doSignup(SignupForm f) {
        // userid(닉네임) 검증/중복
        if (isBlank(f.getUserid()) || !f.getUserid().trim().matches("^[A-Za-z0-9]{4,20}$")) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "msg", "아이디는 영문/숫자 4~20자로 입력하세요."));
        }
        if (userRepository.existsByNickname(f.getUserid().trim())) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "msg", "이미 사용 중인 아이디입니다."));
        }

        // 기본 필수값
        if (isBlank(f.getEmail()) || isBlank(f.getPassword()) || isBlank(f.getPassword2()) || isBlank(f.getName())) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "msg", "필수값 누락"));
        }
        // 비밀번호 8자 이상 + 일치
        if (f.getPassword().length() < 8) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "msg", "비밀번호는 8자 이상이어야 합니다."));
        }
        if (!f.getPassword().equals(f.getPassword2())) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "msg", "비밀번호 불일치"));
        }
        // 이메일 중복
        if (userRepository.findByEmail(f.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "msg", "이미 사용 중인 이메일"));
        }

        Gender gender = "남자".equals(f.getGender()) ? Gender.male
                : "여자".equals(f.getGender()) ? Gender.female : null;

        User user = User.builder()
                .email(f.getEmail())
                .password(passwordEncoder.encode(f.getPassword()))
                .name(f.getName())
                .nickname(f.getUserid().trim()) // 닉네임=userid
                .address(join(f.getAddress(), f.getDetailAddress()))
                .phone(sanitizePhoneKeepHyphen(f.getPhone()))
                .birth_date(!isBlank(f.getBirth()) ? LocalDate.parse(f.getBirth()) : null)
                .gender(gender)
                .role(f.getRole() != null ? f.getRole() : Role.student)
                .status(f.getStatus() != null ? f.getStatus() : UserStatus.active)
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();

        userRepository.save(user);
        return ResponseEntity.ok(Map.of("ok", true, "userId", user.getId()));
    }

    // =========================
    // 로그인 (닉네임만 허용)
    // 폼 제출은 성공 시 303 → "/" 리다이렉트
    // =========================
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> loginForm(@ModelAttribute LoginForm form, HttpSession session) {
        String nickname = form.getId(); // 오직 닉네임만 사용
        ResponseEntity<?> result = doLoginByNickname(nickname, form.getPassword(), session);
        if (!result.getStatusCode().is2xxSuccessful()) return result; // 실패는 JSON 그대로
        return ResponseEntity.status(303)
                .header(HttpHeaders.LOCATION, "/")
                .build();
    }

    // JSON 로그인 (프런트 fetch 등)
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loginJson(@RequestBody LoginForm form, HttpSession session) {
        String nickname = form.getId(); // 오직 닉네임만 사용
        return doLoginByNickname(nickname, form.getPassword(), session);
    }

    private ResponseEntity<?> doLoginByNickname(String nickname, String password, HttpSession session) {
        if (isBlank(nickname) || isBlank(password)) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "msg", "아이디/비밀번호 필요"));
        }
        // 닉네임 형식 검증(이메일 금지)
        if (nickname.contains("@") || !nickname.matches("^[A-Za-z0-9]{4,20}$")) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "msg", "아이디 형식이 올바르지 않습니다."));
        }

        User user = userRepository.findByNickname(nickname).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("ok", false, "msg", "존재하지 않는 계정"));
        }
        boolean matches = passwordEncoder.matches(password, user.getPassword())
                || password.equals(user.getPassword()); // (이행기) 평문 대응
        if (!matches) {
            return ResponseEntity.status(401).body(Map.of("ok", false, "msg", "비밀번호 불일치"));
        }

        session.setAttribute("loginUser", user);
        return ResponseEntity.ok(Map.of(
                "ok", true,
                "userId", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole() != null ? user.getRole().name() : "student"
        ));
    }

    // =========================
    // 세션 확인
    // =========================
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

    // =========================
    // 로그아웃
    // =========================
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("ok", true));
    }

    // =========================
    // DTO
    // =========================
    @lombok.Data
    public static class LoginForm {
        private String id;        // ★ 닉네임만 사용 (이메일 사용 안 함)
        private String email;     // (미사용, 필요 없으면 제거해도 됨)
        private String password;
    }

    @lombok.Data
    public static class SignupForm {
        private String userid;         // 닉네임으로 저장
        private String email;
        private String password;
        private String password2;
        private String name;
        private String birth;          // yyyy-MM-dd
        private String gender;         // '남자' / '여자'
        private String address;
        private String detailAddress;
        private String phone;
        private String authCode;       // (옵션, 미사용)
        private Boolean noteAgree;     // (옵션)
        private Boolean emailAgree;    // (옵션)
        private Role role;             // 기본: student
        private UserStatus status;     // 기본: active
    }

    // =========================
    // Util
    // =========================
    private static boolean isBlank(String s) { return s == null || s.isBlank(); }

    private static String join(String a, String b) {
        if (isBlank(a)) return isBlank(b) ? null : b;
        return isBlank(b) ? a : a + " " + b;
    }

    private static String sanitizePhoneKeepHyphen(String s) {
        if (s == null) return null;
        String t = s.replaceAll("[^0-9-]", ""); // 숫자/하이픈만
        t = t.replaceAll("-{2,}", "-");         // 연속 하이픈 정리
        t = t.replaceAll("^-|-$", "");          // 앞/뒤 하이픈 제거
        return t;
    }
}