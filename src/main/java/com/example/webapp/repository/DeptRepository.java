package com.example.webapp.repository;

import com.example.webapp.entity.Dept;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeptRepository extends JpaRepository<Dept, Long> {
    Optional<Dept> findByName(String name);
}
