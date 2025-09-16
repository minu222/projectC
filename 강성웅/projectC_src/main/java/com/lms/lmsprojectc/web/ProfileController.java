package com.lms.lmsprojectc.web;

import com.lms.lmsprojectc.domain.Gender;
import com.lms.lmsprojectc.domain.Role;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;
    private final InstructorProfileRepository instructorProfileRepository;

    // 화면
    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        User u = (User) session.getAttribute("loginUser");
        if (u == null) return "redirect:/login";

        // DB 최신값 반영
        User fresh = userRepository.findById(u.getId()).orElse(u);
        model.addAttribute("me", fresh);

        InstructorProfile ip = (fresh.getRole() == Role.instructor)
                ? instructorProfileRepository.findById(fresh.getId()).orElse(null)
                : null;
        model.addAttribute("ip", ip);

        return "edit"; // templates/edit.html
    }

    // 업데이트 (x-www-form-urlencoded)
    @PostMapping(value = "/api/profile/update", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    @Transactional
    public ResponseEntity<?> updateProfile(@RequestParam MultiValueMap<String,String> p, HttpSession session) {
        User u = (User) session.getAttribute("loginUser");
        if (u == null) return ResponseEntity.status(401).body(Map.of("ok", false, "msg", "로그인이 필요합니다"));

        User user = userRepository.findById(u.getId()).orElseThrow();

        // 변경 필드
        String nickname = trim(p.getFirst("nickname"));
        String name     = trim(p.getFirst("name"));
        String phone    = trim(p.getFirst("phone"));
        String birth    = trim(p.getFirst("birth"));
        String gender   = trim(p.getFirst("gender"));
        String addr     = trim(p.getFirst("address"));
        String addr2    = trim(p.getFirst("detailAddress"));

        // 비밀번호 변경(선택)
        String newPw    = trim(p.getFirst("newPassword"));
        String newPw2   = trim(p.getFirst("newPassword2"));

        // 닉네임 규칙 & 중복
        if (nickname != null && !nickname.equals(user.getNickname())) {
            if (!nickname.matches("^[A-Za-z0-9]{4,20}$")) {
                return ResponseEntity.badRequest().body(Map.of("ok", false, "msg", "아이디(닉네임)는 영문/숫자 4~20자"));
            }
            boolean exists = userRepository.findByNickname(nickname)
                    .filter(x -> !x.getId().equals(user.getId()))
                    .isPresent();
            if (exists) return ResponseEntity.badRequest().body(Map.of("ok", false, "msg", "이미 사용 중인 아이디"));
            user.setNickname(nickname);
        }

        if (name != null) user.setName(name);
        if (phone != null) user.setPhone(sanitizePhoneKeepHyphen(phone));
        if (addr != null || addr2 != null) user.setAddress(join(addr, addr2));
        if (birth != null && !birth.isBlank()) user.setBirth_date(LocalDate.parse(birth));

        if (gender != null) {
            Gender g = "남자".equals(gender) ? Gender.male
                    : "여자".equals(gender) ? Gender.female
                    : null;
            user.setGender(g);
        }

        // ✅ 비밀번호 평문 저장(요청사항)
        if (newPw != null && !newPw.isBlank()) {
            if (newPw.length() < 8) return ResponseEntity.badRequest().body(Map.of("ok", false, "msg", "비밀번호는 8자 이상"));
            if (!newPw.equals(newPw2)) return ResponseEntity.badRequest().body(Map.of("ok", false, "msg", "비밀번호 확인 불일치"));
            user.setPassword(newPw);
        }

        // 강사 추가 정보
        if (user.getRole() == Role.instructor) {
            String affiliation = trim(p.getFirst("affiliation"));
            String intro       = trim(p.getFirst("intro"));

            InstructorProfile ip = instructorProfileRepository.findById(user.getId()).orElseGet(() -> {
                InstructorProfile created = new InstructorProfile();
                created.setInstructor(user); // @MapsId
                return created;
            });
            if (affiliation != null) ip.setAffiliation(affiliation);
            if (intro != null)       ip.setBio(intro);
            instructorProfileRepository.save(ip);
        }

        userRepository.save(user);

        // 세션 최신화
        session.setAttribute("loginUser", user);

        return ResponseEntity.ok(Map.of("ok", true));
    }

    // (선택) JSON도 받으려면 별도 구현
    @PostMapping(value = "/api/profile/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Transactional
    public ResponseEntity<?> updateProfileJson(@RequestBody Map<String,Object> body, HttpSession session) {
        return ResponseEntity.badRequest().body(Map.of("ok", false, "msg", "JSON은 현재 비활성화됨"));
    }

    // Utils
    private static String trim(String s){ return s==null?null:s.trim(); }

    private static String join(String a, String b){
        if (a==null || a.isBlank()) return (b==null||b.isBlank())?null:b;
        if (b==null || b.isBlank()) return a;
        return a + " " + b;
    }

    private static String sanitizePhoneKeepHyphen(String s) {
        if (s == null) return null;
        String t = s.replaceAll("[^0-9-]", "");
        t = t.replaceAll("-{2,}", "-");
        t = t.replaceAll("^-|-$", "");
        return t.isEmpty()?null:t;
    }
}