package com.lms.adminpages.classrooms.dao;

import com.lms.adminpages.classrooms.entity.MockExam;
import com.lms.adminpages.users.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MockExamDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 전체 조회 (JOIN)
    public List<MockExam> findAll() {
        String sql = """
            SELECT me.exam_id, me.instructor_id, me.student_id, me.title, me.question, me.answer, me.score, me.taken_at,
                   u.nickname AS instructorName
            FROM mock_exams me
            LEFT JOIN users u ON me.instructor_id = u.user_id
            ORDER BY me.exam_id DESC
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> MockExam.builder()
                .examId(rs.getInt("exam_id"))
                .instructorId(rs.getInt("instructor_id"))
                .studentId(rs.getInt("student_id"))
                .title(rs.getString("title"))
                .question(rs.getString("question"))
                .answer(rs.getString("answer"))
                .score(rs.getInt("score"))
                .takenAt(rs.getTimestamp("taken_at"))
                .instructorName(rs.getString("instructorName")) // 화면용
                .build()
        );
    }

    public List<User> findAllInstructors() {
        String sql = "SELECT user_id, nickname FROM users WHERE role='instructor'";
        return jdbcTemplate.query(sql, (rs, rowNum) -> User.builder()
                .user_id(rs.getInt("user_id"))
                .nickname(rs.getString("nickname"))
                .build()
        );
    }

    public List<MockExam> findByInstructorId(int instructorId) {
        String sql = """
        SELECT me.exam_id, me.instructor_id, me.student_id, me.title, me.question, me.answer, me.score, me.taken_at,
               u.nickname AS instructorName
        FROM mock_exams me
        LEFT JOIN users u ON me.instructor_id = u.user_id
        WHERE me.instructor_id = ?
        ORDER BY me.exam_id DESC
    """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> MockExam.builder()
                .examId(rs.getInt("exam_id"))
                .instructorId(rs.getInt("instructor_id"))
                .studentId(rs.getInt("student_id"))
                .title(rs.getString("title"))
                .question(rs.getString("question"))
                .answer(rs.getString("answer"))
                .score(rs.getInt("score"))
                .takenAt(rs.getTimestamp("taken_at"))
                .instructorName(rs.getString("instructorName"))
                .build(), instructorId);
    }


    public MockExam findById(int examId) {
        String sql = """
            SELECT me.exam_id, me.instructor_id, me.student_id, me.title, me.question, me.answer, me.score, me.taken_at,
                   u.nickname AS instructorName
            FROM mock_exams me
            LEFT JOIN users u ON me.instructor_id = u.user_id
            WHERE me.exam_id = ?
        """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> MockExam.builder()
                        .examId(rs.getInt("exam_id"))
                        .instructorId(rs.getInt("instructor_id"))
                        .studentId(rs.getInt("student_id"))
                        .title(rs.getString("title"))
                        .question(rs.getString("question"))
                        .answer(rs.getString("answer"))
                        .score(rs.getInt("score"))
                        .takenAt(rs.getTimestamp("taken_at"))
                        .instructorName(rs.getString("instructorName"))
                        .build()
                , examId);
    }
    private MockExam mapRowToMockExam(ResultSet rs) throws SQLException {
        return MockExam.builder()
                .examId(rs.getInt("exam_id"))
                .instructorId(rs.getInt("instructor_id"))
                .studentId(rs.getInt("student_id"))
                .title(rs.getString("title"))
                .question(rs.getString("question"))
                .answer(rs.getString("answer"))
                .score(rs.getInt("score"))
                .takenAt(rs.getTimestamp("taken_at"))
                .build();
    }

    // 등록
    public void save(MockExam exam) {
        String sql = """
            INSERT INTO mock_exams (instructor_id, student_id, title, question, answer, score)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        jdbcTemplate.update(sql,
                exam.getInstructorId(),
                exam.getStudentId(),
                exam.getTitle(),
                exam.getQuestion(),
                exam.getAnswer(),
                exam.getScore()
        );
    }

    // 수정
    public void update(MockExam exam) {
        String sql = """
            UPDATE mock_exams
            SET instructor_id=?, student_id=?, title=?, question=?, answer=?, score=?
            WHERE exam_id=?
        """;
        jdbcTemplate.update(sql,
                exam.getInstructorId(),
                exam.getStudentId(),
                exam.getTitle(),
                exam.getQuestion(),
                exam.getAnswer(),
                exam.getScore(),
                exam.getExamId()
        );
    }

    // 삭제
    public void delete(int examId) {
        String sql = "DELETE FROM mock_exams WHERE exam_id=?";
        jdbcTemplate.update(sql, examId);
    }
}