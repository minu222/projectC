package com.lms.adminpages.classrooms.dao;

import com.lms.adminpages.classrooms.entity.MockExam;
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
        String sql = "SELECT * FROM mock_exams";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToMockExam(rs));
    }

    public List<MockExam> findByInstructorId(Integer instructorId) {
        String sql = "SELECT * FROM mock_exams WHERE instructor_id = ?";
        return jdbcTemplate.query(sql, new Object[]{instructorId}, (rs, rowNum) -> mapRowToMockExam(rs));
    }


    public MockExam findById(int examId) {
        String sql = "SELECT * FROM mock_exams WHERE exam_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{examId}, (rs, rowNum) -> mapRowToMockExam(rs));
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