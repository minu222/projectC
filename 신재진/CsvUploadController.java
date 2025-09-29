package com.lms;

import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.io.*;
import java.nio.charset.Charset;
import java.sql.Timestamp;

@RestController
@RequestMapping("/api")
public class CsvUploadController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/upload")
    public String uploadCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "파일이 선택되지 않았습니다.";
        }

        try {
            // 1. MultipartFile을 byte[]로 읽기
            byte[] fileBytes = file.getBytes();

            // 2. 인코딩 감지
            Charset charset = detectCharset(fileBytes);

            // 3. ByteArrayInputStream으로 CSVReader 생성
            try (CSVReader reader = new CSVReader(new InputStreamReader(new ByteArrayInputStream(fileBytes), charset))) {

                String[] nextLine;
                reader.readNext(); // 헤더 스킵

                // SQL 준비
                String sql = "INSERT INTO exam_questions (instructor_id, course_title, question, option1, option2, option3, option4, answer, score, taken_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                // CSV에서 2번째 줄 읽기
                nextLine = reader.readNext(); // A2=nickname, B2=course_title, C2~I2
                String nickname = nextLine[0].trim(); // A2 (nickname)

                // nickname → instructor_id 변환
                Integer instructorId = jdbcTemplate.queryForObject(
                        "SELECT instructor_id FROM instructor_profile WHERE nickname = ?",
                        Integer.class, nickname
                );

                String courseTitle = nextLine[1].trim(); // B2

                // 2번째 줄 DB 저장
                if (nextLine.length >= 9 && !nextLine[2].trim().isEmpty()) { // question 체크
                    int score = 0;
                    if (nextLine[8] != null && !nextLine[8].trim().isEmpty()) {
                        score = Integer.parseInt(nextLine[8].trim());
                    }

                    jdbcTemplate.update(sql,
                            instructorId,
                            courseTitle,
                            nextLine[2], nextLine[3], nextLine[4],
                            nextLine[5], nextLine[6], nextLine[7],
                            score, new Timestamp(System.currentTimeMillis()));
                }

                // 나머지 줄 처리
                while ((nextLine = reader.readNext()) != null) {
                    if (nextLine.length < 9 || nextLine[2].trim().isEmpty()) continue; // question 체크

//                    String rowNickname = nextLine[0].trim(); // A열에서 nickname 읽기
//                    Integer rowInstructorId = jdbcTemplate.queryForObject(
//                            "SELECT instructor_id FROM instructor_profile WHERE nickname = ?",
//                            Integer.class, rowNickname
//                    );

                    int score = 0;
                    if (nextLine[8] != null && !nextLine[8].trim().isEmpty()) {
                        score = Integer.parseInt(nextLine[8].trim());
                    }

                    jdbcTemplate.update(sql,
                            instructorId,
                            courseTitle,  // courseTitle (B열)
                            nextLine[2], nextLine[3], nextLine[4],
                            nextLine[5], nextLine[6], nextLine[7],
                            score, new Timestamp(System.currentTimeMillis()));
                }

                return "CSV 데이터 DB 저장 완료! (인코딩: " + charset.displayName() + ")";
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            return "CSV 저장 실패: " + e.getMessage();
        }

    }

    // --- byte[] 기반 인코딩 감지 ---
    private Charset detectCharset(byte[] bytes) {
        if (bytes.length >= 3 &&
                (bytes[0] & 0xFF) == 0xEF &&
                (bytes[1] & 0xFF) == 0xBB &&
                (bytes[2] & 0xFF) == 0xBF) {
            return Charset.forName("UTF-8"); // BOM 있는 UTF-8
        }

        boolean isUtf8 = isUTF8(bytes);
        return isUtf8 ? Charset.forName("UTF-8") : Charset.forName("MS949");
    }

    // --- UTF-8 유효성 검사 ---
    private boolean isUTF8(byte[] input) {
        int i = 0;
        while (i < input.length) {
            int b = input[i] & 0xFF;
            if (b < 0x80) {
                i++;
            } else if ((b >> 5) == 0x6 && i + 1 < input.length && (input[i+1] & 0xC0) == 0x80) {
                i += 2;
            } else if ((b >> 4) == 0xE && i + 2 < input.length &&
                    (input[i+1] & 0xC0) == 0x80 &&
                    (input[i+2] & 0xC0) == 0x80) {
                i += 3;
            } else if ((b >> 3) == 0x1E && i + 3 < input.length &&
                    (input[i+1] & 0xC0) == 0x80 &&
                    (input[i+2] & 0xC0) == 0x80 &&
                    (input[i+3] & 0xC0) == 0x80) {
                i += 4;
            } else {
                return false;
            }
        }
        return true;
    }
}

// =============================
// 다운로드용 컨트롤러 (분리)
@RestController
@RequestMapping("/api")
class FileDownloadController {

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile() throws IOException {
        Resource resource = new ClassPathResource("files/exam.csv");

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"exam.csv\"");

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }
}
