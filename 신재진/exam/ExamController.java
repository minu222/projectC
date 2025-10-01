package com.lms.mainpages.exam;

import com.lms.mainpages.exam.entity.CourseInfoDTO;
import com.lms.mainpages.exam.entity.ExamQuestion;
import com.lms.mainpages.repository.CourseRepository;
import com.lms.mainpages.exam.ExamQuestionRepository;
import com.lms.mainpages.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ExamController {

    private final ExamQuestionRepository examQuestionRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    private final DataSource dataSource;


    private int getExamIdForCourse(String courseTitle) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT DISTINCT exam_id FROM exam_questions WHERE course_title = ? LIMIT 1")) {
            pstmt.setString(1, courseTitle);
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("exam_id");
                } else {
                    throw new IllegalArgumentException("해당 강의에 시험이 존재하지 않습니다. courseTitle=" + courseTitle);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("시험 조회 중 DB 오류 발생", e);
        }
    }
    /** ================== 시험 페이지(GET) ================== */
    @GetMapping("/exam/start/{courseId}")
    public String examPage(
            @PathVariable("courseId") long courseId,
            HttpServletRequest request,
            Model model) {


        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            return "redirect:/login?redirect=/exam/start/" + courseId;
        }

        int studentId = ((Long) session.getAttribute("userId")).intValue();

        // ✅ 강의 정보(제목 + 강사ID) 한 번에 가져오기
        CourseInfoDTO courseInfo = courseRepository.findTitleAndInstructorById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다."));

        String courseTitle = courseInfo.getTitle();
        Long instructorId = courseInfo.getInstructorId();


        // 시험 문제 가져오기
        List<ExamQuestion> questions = examQuestionRepository
                .findByInstructorIdAndCourseTitle(instructorId, courseTitle);

        int examId = getExamIdForCourse(courseTitle); // 실제 로직에 맞게 조회
        model.addAttribute("examId", examId);


        model.addAttribute("questions", questions);
        model.addAttribute("studentId", studentId);
        model.addAttribute("courseId", courseId);
        model.addAttribute("courseTitle", courseTitle);
        model.addAttribute("instructorId", instructorId);

        return "myclass/examspage";
    }

    /** ================== 시험 제출(POST, JDBC 저장) ================== */
    @PostMapping("/submit")
    public String submitExam(HttpServletRequest request, HttpSession session) {
        // 세션에서 학생 ID 가져오기
        Integer studentId = ((Long) session.getAttribute("userId")).intValue();

        // examId 안전하게 처리
        String examIdStr = request.getParameter("examId");
        if (examIdStr == null || examIdStr.isEmpty()) {
            throw new IllegalArgumentException("examId가 전달되지 않았습니다.");
        }
        Integer examId = Integer.parseInt(examIdStr);

        // courseTitle 안전하게 처리
        String courseTitle = request.getParameter("courseTitle");
        if (courseTitle == null || courseTitle.isEmpty()) {
            throw new IllegalArgumentException("courseTitle이 전달되지 않았습니다.");
        }

        // questionCount 안전하게 처리
        String questionCountStr = request.getParameter("questionCount");
        if (questionCountStr == null || questionCountStr.isEmpty()) {
            throw new IllegalArgumentException("questionCount가 전달되지 않았습니다.");
        }
        int questionCount = Integer.parseInt(questionCountStr);

        System.out.println(">>> POST studentId: " + studentId);
        System.out.println(">>> POST examId: " + examId);
        System.out.println(">>> POST courseTitle: " + courseTitle);
        System.out.println(">>> POST questionCount: " + questionCount);

        // 답안 수집
        Map<String, String> answersMap = new HashMap<>();
        for (int i = 0; i < questionCount; i++) {
            String answer = request.getParameter("q" + i);
            answersMap.put("q" + i, answer != null ? answer : "");
        }
        String answersStr = answersMap.toString();
        System.out.println(">>> POST answers: " + answersStr);

        // 점수 계산
        int totalScore = 0;
        List<ExamQuestion> questions = examQuestionRepository.findByCourseTitle(courseTitle);
        for (int i = 0; i < questions.size(); i++) {
            ExamQuestion question = questions.get(i);
            String correctAnswer = question.getAnswer();
            String userAnswer = answersMap.getOrDefault("q" + i, "");
            if (correctAnswer.equals(userAnswer)) {
                totalScore += question.getScore();;
            }
        }
        System.out.println(">>> POST totalScore: " + totalScore);

        // JDBC로 DB 저장
        String sql = "INSERT INTO mock_exam_results (exam_id, course_title, student_id, answers, total_score) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, examId);
            pstmt.setString(2, courseTitle);
            pstmt.setInt(3, studentId);
            pstmt.setString(4, answersStr);
            pstmt.setInt(5, totalScore);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("시험 결과 저장 중 오류 발생", e);
        }

        return "redirect:/myclass/student/exams";
    }


}
