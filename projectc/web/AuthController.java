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
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.crypto.password.PasswordEncoder;

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
    // 회원가입 (폼 전송: x-www-form-urlencoded)
    // =========================
    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> signupForm(@RequestParam MultiValueMap<String, String> params) {
        // 원시 파라미터로 DTO 수동 구성 (바인딩 누락 방지)
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
        // 서버 기본값 강제
        f.setRole(Role.student);
        f.setStatus(UserStatus.active);

        return doSignup(f);
    }

    // =========================
    // 회원가입 (JSON)
    // =========================
    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> signupJson(@RequestBody SignupForm form) {
        // 클라이언트가 role/status를 안 보내도 서버가 기본값 보장
        if (form.getRole() == null)   form.setRole(Role.student);
        if (form.getStatus() == null) form.setStatus(UserStatus.active);
        return doSignup(form);
    }

    private ResponseEntity<?> doSignup(SignupForm f) {
        // 필수값 검증
        if (isBlank(f.getEmail()) || isBlank(f.getPassword()) || isBlank(f.getPassword2()) || isBlank(f.getName())) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "msg", "필수값 누락"));
        }
        if (!f.getPassword().equals(f.getPassword2())) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "msg", "비밀번호 불일치"));
        }
        if (userRepository.findByEmail(f.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "msg", "이미 사용 중인 이메일"));
        }

        Gender gender = "남자".equals(f.getGender()) ? Gender.male
                : "여자".equals(f.getGender()) ? Gender.female : null;

        // 닉네임: userid → name → 이메일 로컬파트 → fallback
        String nickname = firstNonBlank(
                f.getUserid(),
                f.getName(),
                (f.getEmail() != null && f.getEmail().contains("@")) ? f.getEmail().split("@")[0] : null
        );
        if (isBlank(nickname)) nickname = "user" + System.currentTimeMillis();

        User user = User.builder()
                .email(f.getEmail())
                .password(passwordEncoder.encode(f.getPassword()))
                .name(f.getName())
                .nickname(nickname)
                .address(join(f.getAddress(), f.getDetailAddress()))
                .phone(sanitizePhoneKeepHyphen(f.getPhone())) // 숫자만 저장
                .birth_date(!isBlank(f.getBirth()) ? LocalDate.parse(f.getBirth()) : null)
                .gender(gender)
                .role(f.getRole() != null ? f.getRole() : Role.student)            // 기본 보장
                .status(f.getStatus() != null ? f.getStatus() : UserStatus.active) // 기본 보장
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();

        userRepository.save(user);
        return ResponseEntity.ok(Map.of("ok", true, "userId", user.getId()));
    }

    // =========================
    // 로그인 (폼 전송)
    // =========================
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> loginForm(@ModelAttribute LoginForm form, HttpSession session) {
        String idOrEmail = !isBlank(form.getId()) ? form.getId() : form.getEmail();
        ResponseEntity<?> result = doLogin(idOrEmail, form.getPassword(), session);

        // 실패(401/400 등)는 기존 JSON 그대로 반환
        if (!result.getStatusCode().is2xxSuccessful()) return result;

        // 성공 시 303 See Other → GET "/" 로 이동
        return ResponseEntity.status(303)
                .header(HttpHeaders.LOCATION, "/")
                .build();
    }

    // =========================
    // 로그인 (JSON)
    // =========================
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loginJson(@RequestBody LoginForm form, HttpSession session) {
        String idOrEmail = !isBlank(form.getId()) ? form.getId() : form.getEmail();
        return doLogin(idOrEmail, form.getPassword(), session);
    }

    private ResponseEntity<?> doLogin(String idOrEmail, String password, HttpSession session) {
        if (isBlank(idOrEmail) || isBlank(password)) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "msg", "이메일/비밀번호 필요"));
        }
        User user = userRepository.findByEmail(idOrEmail).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("ok", false, "msg", "존재하지 않는 계정"));
        }
        boolean matches = passwordEncoder.matches(password, user.getPassword())
                || password.equals(user.getPassword()); // (이행기) 평문 저장 대응
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
        private String id;       // login.html의 id(=이메일)
        private String email;    // JSON일 때 이메일 키 사용 가능
        private String password;
    }

    @lombok.Data
    public static class SignupForm {
        private String userid;
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

        // 서버에서 기본값 강제
        private Role role;
        private UserStatus status;
    }

    // =========================
    // Util
    // =========================
    private static boolean isBlank(String s) { return s == null || s.isBlank(); }

    private static String join(String a, String b) {
        if (isBlank(a)) return isBlank(b) ? null : b;
        return isBlank(b) ? a : a + " " + b;
    }

    private static String firstNonBlank(String... vals) {
        if (vals != null) for (String v : vals) if (v != null && !v.isBlank()) return v;
        return null;
    }

    private static String sanitizePhoneKeepHyphen(String s) {
        if (s == null) return null;
        String t = s.replaceAll("[^0-9-]", "");   // 숫자/하이픈만 남김
        t = t.replaceAll("-{2,}", "-");           // 연속 하이픈은 하나로
        t = t.replaceAll("^-|-$", "");            // 앞뒤 하이픈 제거
        return t;
    }
}