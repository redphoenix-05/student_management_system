package com.example.webapp.service;

import com.example.webapp.entity.Dept;
import com.example.webapp.repository.DeptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeptService {
    
    @Autowired
    private DeptRepository deptRepository;

    public List<Dept> getAllDepartments() {
        return deptRepository.findAll();
    }

    public Optional<Dept> getDepartmentById(Long id) {
        return deptRepository.findById(id);
    }

    public Dept saveDepartment(Dept dept) {
        return deptRepository.save(dept);
    }

    public void deleteDepartment(Long id) {
        deptRepository.deleteById(id);
    }

    public Optional<Dept> findByName(String name) {
        return deptRepository.findByName(name);
    }
}
