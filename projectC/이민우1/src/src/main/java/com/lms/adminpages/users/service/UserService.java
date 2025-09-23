package com.lms.adminpages.users.service;

import com.lms.adminpages.users.dao.UserDao;
import com.lms.adminpages.users.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    // 상태, 소속, 검색어 필터 적용한 강사 목록
    public List<User> getInstructors(String status, String department, String keywordType, String keyword) {
        System.out.println(userDao.findInstructors(status, department, keywordType, keyword));
        return userDao.findInstructors(status, department, keywordType, keyword);
    }



    public List<User> getStudents(String status, String department, String keywordType, String keyword) {
        return userDao.findStudents(status, department, keywordType, keyword);
    }

    public void deleteUsers(int[] ids) {
        userDao.updateStatusToDeleted(ids);
    }

}
