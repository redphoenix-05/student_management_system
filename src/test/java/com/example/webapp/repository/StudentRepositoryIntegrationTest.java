package com.example.webapp.repository;

import com.example.webapp.entity.Dept;
import com.example.webapp.entity.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Student Repository Integration Tests")
class StudentRepositoryIntegrationTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DeptRepository deptRepository;

    private Dept dept;
    private Student student1;
    private Student student2;

    @BeforeEach
    void setUp() {
        // Clean up
        studentRepository.deleteAll();
        deptRepository.deleteAll();

        // Create and save department
        dept = new Dept();
        dept.setName("Computer Science");
        dept = deptRepository.save(dept);

        // Create students
        student1 = new Student();
        student1.setName("John Doe");
        student1.setRoll("2021001");
        student1.setEmail("john@example.com");
        student1.setCurrentSemester(5);
        student1.setAcademicYear("2024-2025");
        student1.setDept(dept);
        student1 = studentRepository.save(student1);

        student2 = new Student();
        student2.setName("Jane Smith");
        student2.setRoll("2021002");
        student2.setEmail("jane@example.com");
        student2.setCurrentSemester(5);
        student2.setAcademicYear("2024-2025");
        student2.setDept(dept);
        student2 = studentRepository.save(student2);
    }

    @Test
    @DisplayName("Should find all students")
    void testFindAll() {
        // Act
        List<Student> students = studentRepository.findAll();

        // Assert
        assertNotNull(students);
        assertTrue(students.size() >= 2);
    }

    @Test
    @DisplayName("Should find student by id")
    void testFindById() {
        // Act
        Optional<Student> found = studentRepository.findById(student1.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals("John Doe", found.get().getName());
        assertEquals("2021001", found.get().getRoll());
    }

    @Test
    @DisplayName("Should find student by roll number")
    void testFindByRoll() {
        // Act
        Optional<Student> found = studentRepository.findByRoll("2021001");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("John Doe", found.get().getName());
        assertEquals("john@example.com", found.get().getEmail());
    }

    @Test
    @DisplayName("Should return empty when roll not found")
    void testFindByRoll_NotFound() {
        // Act
        Optional<Student> found = studentRepository.findByRoll("9999999");

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should save new student")
    void testSave() {
        // Arrange
        Student newStudent = new Student();
        newStudent.setName("Bob Johnson");
        newStudent.setRoll("2021003");
        newStudent.setEmail("bob@example.com");
        newStudent.setCurrentSemester(3);
        newStudent.setAcademicYear("2024-2025");
        newStudent.setDept(dept);

        // Act
        Student saved = studentRepository.save(newStudent);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("Bob Johnson", saved.getName());
        assertEquals("2021003", saved.getRoll());
    }

    @Test
    @DisplayName("Should update existing student")
    void testUpdate() {
        // Arrange
        Student existingStudent = studentRepository.findById(student1.getId()).orElseThrow();
        existingStudent.setName("John Updated");
        existingStudent.setEmail("john.updated@example.com");
        existingStudent.setCurrentSemester(6);

        // Act
        Student updated = studentRepository.save(existingStudent);

        // Assert
        assertEquals("John Updated", updated.getName());
        assertEquals("john.updated@example.com", updated.getEmail());
        assertEquals(6, updated.getCurrentSemester());
        assertEquals("2021001", updated.getRoll()); // unchanged
    }

    @Test
    @DisplayName("Should delete student by id")
    void testDeleteById() {
        // Arrange
        Long studentId = student1.getId();

        // Act
        studentRepository.deleteById(studentId);

        // Assert
        Optional<Student> deleted = studentRepository.findById(studentId);
        assertFalse(deleted.isPresent());
    }

    @Test
    @DisplayName("Should enforce unique constraint on roll number")
    void testUniqueRollConstraint() {
        // Arrange
        Student duplicateStudent = new Student();
        duplicateStudent.setName("Another Student");
        duplicateStudent.setRoll("2021001"); // duplicate roll
        duplicateStudent.setEmail("another@example.com");
        duplicateStudent.setCurrentSemester(3);
        duplicateStudent.setDept(dept);

        // Act & Assert
        assertThrows(Exception.class, () -> {
            studentRepository.saveAndFlush(duplicateStudent);
        });
    }

    @Test
    @DisplayName("Should count students correctly")
    void testCount() {
        // Act
        long count = studentRepository.count();

        // Assert
        assertTrue(count >= 2);
    }

    @Test
    @DisplayName("Should check if student exists by id")
    void testExistsById() {
        // Act
        boolean exists = studentRepository.existsById(student1.getId());
        boolean notExists = studentRepository.existsById(999L);

        // Assert
        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    @DisplayName("Should persist student with department relationship")
    void testStudentDepartmentRelationship() {
        // Act
        Optional<Student> found = studentRepository.findById(student1.getId());

        // Assert
        assertTrue(found.isPresent());
        assertNotNull(found.get().getDept());
        assertEquals("Computer Science", found.get().getDept().getName());
    }
}
