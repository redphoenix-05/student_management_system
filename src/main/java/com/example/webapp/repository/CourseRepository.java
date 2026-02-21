package com.example.webapp.repository;

import com.example.webapp.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCode(String code);
    List<Course> findByDeptId(Long deptId);
    List<Course> findByCreatedById(Long teacherId);
}
