package com.lms.adminpages.classrooms.service;

import com.lms.adminpages.classrooms.dao.CourseMaterialDAO;
import com.lms.adminpages.classrooms.entity.CourseMaterial;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseMaterialService {

    private final CourseMaterialDAO courseMaterialDao;

    public CourseMaterialService(CourseMaterialDAO courseMaterialDao) {
        this.courseMaterialDao = courseMaterialDao;
    }

    public List<CourseMaterial> getAllMaterials() {
        return courseMaterialDao.findAll();
    }

    public List<CourseMaterial> getMaterialsByCourse(String courseTitle) {
        return courseMaterialDao.findByCourseTitle(courseTitle);
    }

    public List<CourseMaterial> searchMaterials(String keyword) {
        return courseMaterialDao.searchMaterials(keyword);
    }

    public CourseMaterial getMaterial(Integer id) {
        return courseMaterialDao.findById(id);
    }

    public void saveMaterial(CourseMaterial material) {
        courseMaterialDao.save(material);
    }

    public void updateMaterial(CourseMaterial material) {
        courseMaterialDao.update(material);
    }

    public void deleteMaterial(Integer id) {
        courseMaterialDao.delete(id);
    }
}