package com.lms.projectc.users.repository;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.Optional;

@Repository
public class InstructorProfileRepository {

    private final JdbcTemplate jdbc;

    public InstructorProfileRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * instructor_profile 업서트 (DB-agnostic)
     * - 먼저 UPDATE 시도 → 변경된 행이 없으면 INSERT
     * - 경합으로 INSERT 중복이 발생하면 UPDATE 재시도
     */
    public void upsert(long instructorId, String affiliation, String bio) {
        final String updateSql =
                "UPDATE instructor_profile SET affiliation = ?, bio = ? WHERE instructor_id = ?";

        int updated = jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(updateSql);
            int i = 1;
            if (isBlank(affiliation)) ps.setNull(i++, Types.VARCHAR); else ps.setString(i++, affiliation.trim());
            if (isBlank(bio))         ps.setNull(i++, Types.VARCHAR); else ps.setString(i++, bio.trim());
            ps.setLong(i++, instructorId);
            return ps;
        });

        if (updated > 0) return;

        final String insertSql =
                "INSERT INTO instructor_profile (instructor_id, affiliation, bio) VALUES (?, ?, ?)";

        try {
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(insertSql);
                int i = 1;
                ps.setLong(i++, instructorId);
                if (isBlank(affiliation)) ps.setNull(i++, Types.VARCHAR); else ps.setString(i++, affiliation.trim());
                if (isBlank(bio))         ps.setNull(i++, Types.VARCHAR); else ps.setString(i++, bio.trim());
                return ps;
            });
        } catch (DuplicateKeyException e) {
            // 경합으로 이미 누군가 INSERT 한 경우 → UPDATE 한 번 더
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(updateSql);
                int i = 1;
                if (isBlank(affiliation)) ps.setNull(i++, Types.VARCHAR); else ps.setString(i++, affiliation.trim());
                if (isBlank(bio))         ps.setNull(i++, Types.VARCHAR); else ps.setString(i++, bio.trim());
                ps.setLong(i++, instructorId);
                return ps;
            });
        }
    }

    public Optional<InstructorProfile> findByInstructorId(long instructorId) {
        String sql = "SELECT instructor_id, affiliation, bio FROM instructor_profile WHERE instructor_id = ?";
        return jdbc.query(sql, rowMapper(), instructorId).stream().findFirst();
    }

    public int deleteByInstructorId(long instructorId) {
        return jdbc.update("DELETE FROM instructor_profile WHERE instructor_id = ?", instructorId);
    }

    private RowMapper<InstructorProfile> rowMapper() {
        return (rs, rowNum) -> new InstructorProfile(
                rs.getLong("instructor_id"),
                rs.getString("affiliation"),
                rs.getString("bio")
        );
    }

    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    // 간단 DTO (별도 엔티티 클래스가 있으면 그걸 사용하세요)
    public static class InstructorProfile {
        public final long instructorId;
        public final String affiliation;
        public final String bio;

        public InstructorProfile(long instructorId, String affiliation, String bio) {
            this.instructorId = instructorId;
            this.affiliation = affiliation;
            this.bio = bio;
        }
    }
}
