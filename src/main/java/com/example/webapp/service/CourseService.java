package com.example.webapp.service;

import com.example.webapp.entity.Course;
import com.example.webapp.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    
    @Autowired
    private CourseRepository courseRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    public List<Course> getCoursesByDept(Long deptId) {
        return courseRepository.findByDeptId(deptId);
    }

    public List<Course> getCoursesByTeacher(Long teacherId) {
        return courseRepository.findByCreatedById(teacherId);
    }

    public Optional<Course> findByCode(String code) {
        return courseRepository.findByCode(code);
    }
}
