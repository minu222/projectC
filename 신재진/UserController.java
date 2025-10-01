package com.lms.mainpages.controller;

import com.lms.mainpages.entity.User;
import com.lms.mainpages.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * layout.html을 사용하지 않는 로그인 화면(login.html) 기준 컨트롤러.
 * - 세션 키: "userId" (양수일 때만 로그인으로 인정)
 */
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** 로그인 페이지 (login.html 직접 렌더) */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "redirect", required = false) String redirect,
                            Model model) {
        model.addAttribute("redirect", redirect);
        return "login";
    }

    /** /user/login (alias) */
    @GetMapping("/user/login")
    public String loginAlias(@RequestParam(value = "redirect", required = false) String redirect,
                             Model model) {
        return loginPage(redirect, model);
    }

    /** 로그인 처리 */
    @PostMapping("/user/login")
    public String login(@RequestParam("nickname") String nickname,
                        @RequestParam("password") String password,
                        @RequestParam(value = "redirect", required = false) String redirect,
                        HttpServletRequest request,
                        RedirectAttributes ra) {

        return userService.login(nickname, password)
                .map(u -> {
                    Long uid = resolveUserIdFrom(u); // 다양한 타입(User/DTO/record/Map 등) 대응

                    // ✅ userId가 없거나 0/음수면 실패로 간주(세션에 올리지 않음)
                    if (uid == null || uid <= 0) {
                        ra.addFlashAttribute("errorMessage", "로그인 처리 오류: 사용자 ID를 확인할 수 없습니다.");
                        String back = "/login" + (redirect != null && !redirect.isBlank() ? "?redirect=" + redirect : "");
                        return "redirect:" + back;
                    }

                    HttpSession session = request.getSession(true);
                    session.setAttribute("userId", uid);   // 🔑 인터셉터가 보는 유일 키
                    session.setAttribute("loginUser", u);  // (선택) 화면 표시용

                    // nickname이 "admin"이면 관리자 페이지로 리다이렉트
                    if ("admin".equals(nickname)) {
                        return "redirect:/admin/home";
                    }

                    String target = (redirect != null && !redirect.isBlank()) ? redirect : "/";
                    return "redirect:" + target;



                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("errorMessage", "아이디 또는 비밀번호가 올바르지 않습니다.");
                    String back = "/login" + (redirect != null && !redirect.isBlank() ? "?redirect=" + redirect : "");
                    return "redirect:" + back;
                });
    }

    /**
     * ========= 회원가입 폼 =========
     */
    @GetMapping("/user/add")
    public String signupForm(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new SignupForm());
        }
        return "signup"; // templates/user/add.html 로 매핑
    }

    /**
     * ========= 회원가입 처리 =========
     */
    @PostMapping("/user/add")
    public String signup(@Valid SignupForm form, RedirectAttributes ra) {
        // 비밀번호 확인
        if (!Objects.equals(form.getPassword(), form.getPassword2())) {
            ra.addFlashAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
            ra.addFlashAttribute("form", form);
            return "redirect:/user/add";
        }

        // 폰 정리(숫자/하이픈만 유지)
        if (form.getPhone() != null) {
            String p = form.getPhone().replaceAll("[^\\d-]", "").replaceAll("-{2,}", "-");
            form.setPhone(p);
        }

        // 기본값 보정
        if (form.getRole() == null) form.setRole(User.Role.STUDENT);

        try {
            // 서비스에 저장 위임 (구현체에서 INSERT 처리)
            User u = new User();
            u.setNickname(form.getNickname());
            u.setPassword(form.getPassword()); // 실제 배포에선 반드시 해시 처리
            u.setEmail(form.getEmail());
            u.setName(form.getName());
            u.setBirth_day(form.getBirth_day());
            u.setGender(form.getGender());
            u.setAddress(form.getAddress());
            // detailAddress를 DB에 저장할 필드가 따로 있으면 거기에 넣으세요
            u.setPhone(form.getPhone());
            u.setRole(form.getRole());

            userService.register(u, form.getAffiliation(), form.getBio()); // 필요 메서드 형태에 맞게 조정

            ra.addFlashAttribute("successMessage", "회원가입이 완료되었습니다. 로그인 해주세요.");
            return "redirect:/login";
        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("errorMessage", "회원가입 처리 중 오류가 발생했습니다.");
            ra.addFlashAttribute("form", form);
            return "redirect:/user/add";
        }
    }

    /**
     * ===== 폼 DTO =====
     */
    @Getter
    @Setter
    public static class SignupForm {
        private String nickname;
        private String password;
        private String password2;
        private String email;
        private String name;
        private LocalDate birth_day;
        private User.Gender gender;         // "MALE"/"FEMALE" (숨은 input #gender로 셋)
        private String address;
        private String detailAddress;
        private String phone;
        private User.Role role;             // "STUDENT"/"INSTRUCTOR" (숨은 input #role로 셋)
        private String affiliation;         // 강사 전용
        private String bio;                 // 강사 전용
        private boolean noteAgree;
        private boolean emailAgree;
    }


    /** 로그아웃 (POST) */
    @PostMapping("/user/logout")
    public String logout(HttpServletRequest request) {
        HttpSession s = request.getSession(false);
        if (s != null) s.invalidate();
        return "redirect:/";
    }

    /** 로그아웃 (GET, 링크 호출용) */
    @GetMapping("/user/logout")
    public String logoutGet(HttpServletRequest request) {
        HttpSession s = request.getSession(false);
        if (s != null) s.invalidate();
        return "redirect:/";
    }

    /* ====================== 유틸: 객체에서 사용자 ID 추출 ====================== */

    private static final List<String> ID_KEY_PRIORITY = List.of(
            "user_id", "userid", "userId", "id", "memberId", "member_id",
            "uid", "userNo", "user_no", "no", "seq", "idx"
    );
    private static final Pattern ID_NAME_HINT =
            Pattern.compile("(^|_)?(user)?(member)?(account)?(id|no|seq|idx)s?($)", Pattern.CASE_INSENSITIVE);

    private Long resolveUserIdFrom(Object u) {
        if (u == null) return null;

        // 1) 숫자/문자 자체
        if (u instanceof Number n) return n.longValue();
        if (u instanceof String s) { Long v = parseLongSafe(s); if (v != null) return v; }

        // 2) Map 형태 (MyBatis의 Map 결과 등) - 키 대소문자/스네이크/카멜 모두 대응
        if (u instanceof Map<?,?> m) {
            // 우선순위 키부터 시도
            for (String key : ID_KEY_PRIORITY) {
                Object val = findMapValue(m, key);
                Long id = toLong(val);
                if (id != null) return id;
            }
            // 힌트 정규식으로 모든 키 스캔
            for (Object k : m.keySet()) {
                String key = String.valueOf(k);
                if (ID_NAME_HINT.matcher(key).find()) {
                    Long id = toLong(m.get(k));
                    if (id != null) return id;
                }
            }
        }

        // 3) 접근자 메서드(POJO/record) 전수 스캔
        for (Method method : u.getClass().getMethods()) {
            if (method.getParameterCount() == 0) {
                String name = method.getName();
                if (name.startsWith("get") || name.matches("(?i)(id|userId|user_id|userid|memberId|uid|no|seq|idx)")) {
                    if (ID_NAME_HINT.matcher(name).find()) {
                        try {
                            Object val = method.invoke(u);
                            Long id = toLong(val);
                            if (id != null) return id;
                        } catch (Exception ignore) {}
                    }
                }
            }
        }

        // 4) 필드 스캔(스네이크/카멜 상관없이)
        for (Field field : u.getClass().getDeclaredFields()) {
            String name = field.getName();
            if (ID_NAME_HINT.matcher(name).find()) {
                try {
                    field.setAccessible(true);
                    Object val = field.get(u);
                    Long id = toLong(val);
                    if (id != null) return id;
                } catch (Exception ignore) {}
            }
        }

        return null;
    }

    /* -------------------- helpers -------------------- */

    private Object findMapValue(Map<?,?> m, String key) {
        // 키를 대소문자/스네이크/카멜 무시하고 검색
        String target = normalizeName(key);
        for (Map.Entry<?,?> e : m.entrySet()) {
            if (normalizeName(String.valueOf(e.getKey())).equals(target)) {
                return e.getValue();
            }
        }
        // contains 'id' 류 힌트 키 찾기
        for (Map.Entry<?,?> e : m.entrySet()) {
            if (ID_NAME_HINT.matcher(String.valueOf(e.getKey())).find()) {
                return e.getValue();
            }
        }
        return null;
    }

    private String normalizeName(String s) {
        if (s == null) return "";
        return s.replaceAll("[^A-Za-z0-9]", "").toLowerCase(Locale.ROOT);
    }

    private Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.longValue();
        if (v instanceof BigInteger bi) return bi.longValue();
        if (v instanceof BigDecimal bd) return bd.longValue();
        if (v instanceof String s) return parseLongSafe(s);
        return null;
    }

    private Long parseLongSafe(String s) {
        if (s == null) return null;
        try {
            String trimmed = s.trim();
            // 숫자만 있는 경우만 허용 (UUID 등은 제외)
            if (!trimmed.matches("^-?\\d+$")) return null;
            return Long.parseLong(trimmed);
        } catch (Exception e) {
            return null;
        }
    }
}
