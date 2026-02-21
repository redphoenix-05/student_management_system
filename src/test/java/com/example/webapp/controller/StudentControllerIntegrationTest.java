package com.example.webapp.controller;

import com.example.webapp.entity.Dept;
import com.example.webapp.entity.Student;
import com.example.webapp.repository.DeptRepository;
import com.example.webapp.repository.StudentRepository;
import com.example.webapp.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Student Controller Integration Tests")
class StudentControllerIntegrationTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DeptRepository deptReposatory;

    private Dept dept;
    private Student student;

    @BeforeEach
    void setUp() {
        // Clean up
        studentRepository.deleteAll();
        deptReposatory.deleteAll();

        // Setup test data
        dept = new Dept();
        dept.setName("Computer Science");
        dept = deptReposatory.save(dept);

        student = new Student();
        student.setName("John Doe");
        student.setRoll("2021001");
        student.setEmail("john@example.com");
        student.setCurrentSemester(5);
        student.setDept(dept);
        student = studentRepository.save(student);
    }

    @Test
    @DisplayName("Should retrieve all students via service")
    void testGetAllStudents() {
        // Act
        List<Student> students = studentService.getAllStudents();

        // Assert
        assertNotNull(students);
        assertFalse(students.isEmpty());
        assertTrue(students.stream().anyMatch(s -> s.getName().equals("John Doe")));
    }

    @Test
    @DisplayName("Should retrieve student by id via service")
    void testGetStudentById() {
        // Act
        var foundStudent = studentService.getStudentById(student.getId());

        // Assert
        assertTrue(foundStudent.isPresent());
        assertEquals("John Doe", foundStudent.get().getName());
        assertEquals("2021001", foundStudent.get().getRoll());
    }

    @Test
    @DisplayName("Should return empty for non-existent student")
    void testGetStudentById_NotFound() {
        // Act
        var foundStudent = studentService.getStudentById(99999L);

        // Assert
        assertFalse(foundStudent.isPresent());
    }

    @Test
    @DisplayName("Should create new student")
    void testSaveStudent() {
        // Arrange
        long initialCount = studentRepository.count();

        Student newStudent = new Student();
        newStudent.setName("Jane Smith");
        newStudent.setRoll("2021002");
        newStudent.setEmail("jane@example.com");
        newStudent.setCurrentSemester(5);
        newStudent.setDept(dept);

        // Act
        Student savedStudent = studentRepository.save(newStudent);

        // Assert
        assertNotNull(savedStudent.getId());
        assertEquals(initialCount + 1, studentRepository.count());
        assertEquals("Jane Smith", savedStudent.getName());
    }

    @Test
    @DisplayName("Should find student by roll number")
    void testFindStudentByRoll() {
        // Act
        var foundStudent = studentRepository.findByRoll("2021001");

        // Assert
        assertTrue(foundStudent.isPresent());
        assertEquals("John Doe", foundStudent.get().getName());
        assertEquals("john@example.com", foundStudent.get().getEmail());
    }

    @Test
    @DisplayName("Should update existing student")
    void testUpdateStudent() {
        // Arrange
        Student existingStudent = studentRepository.findById(student.getId()).orElseThrow();
        existingStudent.setName("John Updated");
        existingStudent.setEmail("john.updated@example.com");

        // Act
        Student updatedStudent = studentRepository.save(existingStudent);

        // Assert
        assertEquals("John Updated", updatedStudent.getName());
        assertEquals("john.updated@example.com", updatedStudent.getEmail());
        assertEquals("2021001", updatedStudent.getRoll()); // unchanged
    }

    @Test
    @DisplayName("Should delete student")
    void testDeleteStudent() {
        // Arrange
        Long studentId = student.getId();
        long initialCount = studentRepository.count();

        // Act
        studentRepository.deleteById(studentId);

        // Assert
        assertEquals(initialCount - 1, studentRepository.count());
        assertFalse(studentRepository.findById(studentId).isPresent());
    }

    @Test
    @DisplayName("Should enforce unique roll constraint")
    void testUniqueRollConstraint() {
        // Arrange
        Student duplicateStudent = new Student();
        duplicateStudent.setName("Another Student");
        duplicateStudent.setRoll("2021001"); // duplicate roll
        duplicateStudent.setEmail("another@example.com");
        duplicateStudent.setDept(dept);

        // Act & Assert
        assertThrows(Exception.class, () -> {
            studentRepository.saveAndFlush(duplicateStudent);
        });
    }
}
