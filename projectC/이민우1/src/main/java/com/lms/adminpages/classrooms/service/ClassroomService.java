package com.lms.adminpages.classrooms.service;

import com.lms.adminpages.classrooms.dao.ClassroomDAO;
import com.lms.adminpages.classrooms.entity.Classroom;
import com.lms.adminpages.classrooms.entity.CourseFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public void updateStatus(Integer classroomId, String status) {
        if (!List.of("draft", "published", "closed").contains(status)) {
            throw new IllegalArgumentException("잘못된 상태 값입니다.");
        }
        classroomDao.updateStatus(classroomId, status);
    }

    @Transactional
    public void softDeleteByIds(List<Integer> ids) {
        classroomDao.softDeleteByIds(ids);
    }
}

