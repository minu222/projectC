package com.lms.adminpages.classrooms.dao;

import com.lms.adminpages.classrooms.entity.Classroom;
import com.lms.adminpages.classrooms.entity.CourseFilter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class ClassroomDAO {

    private final JdbcTemplate jdbcTemplate;

    public ClassroomDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 강의실(강좌) 저장
    public void save(Classroom classroom) {
        String sql = "INSERT INTO courses (instructor_id, title, description, category, price, is_free, avg_rating, status, student_count, expiry_date, live_limit) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                classroom.getInstructorId(),
                classroom.getTitle(),
                classroom.getDescription(),
                classroom.getCategory(),
                classroom.getPrice(),
                classroom.getIsFree(),
                classroom.getAvgRating(),
                classroom.getStatus(),
                classroom.getStudentCount(),
                classroom.getExpiryDate(),
                classroom.getLiveLimit()
        );
    }

    // 모든 카테고리 조회
    public List<String> findAllCategories() {
        String sql = "SELECT DISTINCT category FROM courses";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    public List<Classroom> findByFilterFromDB(CourseFilter filter) {
        StringBuilder sql = new StringBuilder(
                "SELECT course_id, instructor_id, title, category, status, student_count FROM courses WHERE 1=1"
        );
        List<Object> params = new ArrayList<>();

        if (filter.getCategory() != null && !filter.getCategory().isEmpty()) {
            sql.append(" AND category = ?");
            params.add(filter.getCategory());
        }
        if (filter.getStatus() != null && !filter.getStatus().isEmpty()) {
            sql.append(" AND status = ?");
            params.add(filter.getStatus());
        }
        if (filter.getKeyword() != null && !filter.getKeyword().isEmpty()) {
            sql.append(" AND title LIKE ?");
            params.add("%" + filter.getKeyword() + "%");
        }

        sql.append(" ORDER BY course_id DESC");

        return jdbcTemplate.query(sql.toString(), params.toArray(), (rs, rowNum) -> Classroom.builder()
                .classroomId(rs.getInt("course_id"))
                .title(rs.getString("title"))
                .category(rs.getString("category"))
                .status(rs.getString("status"))
                .studentCount(rs.getInt("student_count"))
                .instructorId(rs.getInt("instructor_id"))
                .build()
        );
    }

    public List<Classroom> findAll() {
        String sql = "SELECT course_id, title, category, status, student_count, instructor_id " +
                "FROM courses WHERE deleted_at IS NULL ORDER BY course_id DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> Classroom.builder()
                .classroomId(rs.getInt("course_id"))
                .title(rs.getString("title"))
                .category(rs.getString("category"))
                .status(rs.getString("status"))
                .studentCount(rs.getInt("student_count"))
                .instructorId(rs.getInt("instructor_id"))
                .build()
        );
    }


    public Classroom findByName(String title) {
        String sql = "SELECT course_id, title, category, status, student_count, instructor_id " +
                "FROM courses WHERE title LIKE ? AND deleted_at IS NULL LIMIT 1";
        List<Classroom> result = jdbcTemplate.query(sql, new Object[]{"%" + title + "%"}, (rs, rowNum) ->
                Classroom.builder()
                        .classroomId(rs.getInt("course_id"))
                        .title(rs.getString("title"))
                        .category(rs.getString("category"))
                        .status(rs.getString("status"))
                        .studentCount(rs.getInt("student_count"))
                        .instructorId(rs.getInt("instructor_id"))
                        .build()
        );
        return result.isEmpty() ? null : result.get(0);
    }


    // 단일 강의실 상태 업데이트
    public void updateStatus(Integer classroomId, String status) {
        String sql = "UPDATE courses SET status = ? WHERE course_id = ?";
        jdbcTemplate.update(sql, status, classroomId);
    }

    // 선택 삭제
    public void deleteByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return;

        String inSql = String.join(",", ids.stream().map(id -> "?").toArray(String[]::new));
        String sql = "UPDATE courses SET deleted_at = NOW() WHERE course_id IN (" + inSql + ")";

        jdbcTemplate.update(sql, ids.toArray());
    }
}


