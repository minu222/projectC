package com.lms.gitprojectc.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class HomeService {

    public int getStudentCount() {
        return 120;
    }

    public int getTeacherCount() {
        return 35;
    }

    public int getCourseCount() {
        return 25;
    }

    public int getAvgCompletion() {
        return 85;
    }

    public int getCancelRate() {
        return 12;
    }

    public int getMonthRevenue() {
        return 12530000;
    }

    public List<String> getRecentNotices() {
        return Arrays.asList(
                "강사 김민수 강의자료 업로드 요청",
                "학생 이영희 수강 연장 신청",
                "관리자 공지: 서버 점검 예정"
        );
    }
}
