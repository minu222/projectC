package com.lms.adminpages.classrooms.service;

import com.lms.adminpages.classrooms.dao.ClassroomDAO;
import com.lms.adminpages.classrooms.entity.Classroom;
import com.lms.adminpages.classrooms.entity.CourseFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class ClassroomService {

    private final ClassroomDAO classroomDao;

    public ClassroomService(ClassroomDAO classroomDao) {
        this.classroomDao = classroomDao;
    }

    @Transactional
    public void save(Classroom classroom) {
        classroomDao.save(classroom);
    }


    public List<String> findAllCategories() {
        return classroomDao.findAllCategories();
    }

    public List<Classroom> findByFilterFromDB(CourseFilter filter) {
        return classroomDao.findByFilterFromDB(filter);
    }

    public List<Classroom> findAll() {
        return classroomDao.findAll();
    }


    //상태 수정
    @Transactional
    public void updateStatus(Map<Integer, String> statusMap) {
        if (statusMap == null || statusMap.isEmpty()) return;
        statusMap.forEach(classroomDao::updateStatus);
    }

    // 선택 삭제
    @Transactional
    public void deleteByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return;
        classroomDao.deleteByIds(ids);
    }
}

