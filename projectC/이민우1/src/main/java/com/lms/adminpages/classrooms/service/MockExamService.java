package com.lms.adminpages.classrooms.service;

import com.lms.adminpages.classrooms.dao.MockExamDAO;
import com.lms.adminpages.classrooms.entity.MockExam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MockExamService {

    @Autowired
    private MockExamDAO mockExamDao;



    public List<MockExam> getAllExams() {
        return mockExamDao.findAll();
    }

    public List<MockExam> searchExamsByInstructorId(Integer instructorId) {
        return mockExamDao.findByInstructorId(instructorId);
    }

    public MockExam getExamById(int examId) {
        return mockExamDao.findById(examId);
    }


    public void saveExam(MockExam exam) {
        mockExamDao.save(exam);
    }

    public void updateExam(MockExam exam) {
        mockExamDao.update(exam);
    }

    public void deleteExam(int examId) {
        mockExamDao.delete(examId);
    }
}