package com.example.webapp.service;

import com.example.webapp.entity.Dept;
import com.example.webapp.entity.Student;
import com.example.webapp.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Student Service Unit Tests")
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student student1;
    private Student student2;
    private Dept dept;

    @BeforeEach
    void setUp() {
        dept = new Dept();
        dept.setId(1L);
        dept.setName("Computer Science");

        student1 = new Student();
        student1.setId(1L);
        student1.setName("John Doe");
        student1.setRoll("2021001");
        student1.setEmail("john@example.com");
        student1.setCurrentSemester(5);
        student1.setDept(dept);

        student2 = new Student();
        student2.setId(2L);
        student2.setName("Jane Smith");
        student2.setRoll("2021002");
        student2.setEmail("jane@example.com");
        student2.setCurrentSemester(5);
        student2.setDept(dept);
    }

    @Test
    @DisplayName("Should retrieve all students")
    void testGetAllStudents() {
        // Arrange
        when(studentRepository.findAll()).thenReturn(Arrays.asList(student1, student2));

        // Act
        List<Student> result = studentService.getAllStudents();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("Jane Smith", result.get(1).getName());
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve student by id when exists")
    void testGetStudentById_WhenExists() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));

        // Act
        Optional<Student> result = studentService.getStudentById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
        assertEquals("2021001", result.get().getRoll());
        verify(studentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when student not found")
    void testGetStudentById_WhenNotExists() {
        // Arrange
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Student> result = studentService.getStudentById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(studentRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should save student successfully")
    void testSaveStudent() {
        // Arrange
        when(studentRepository.save(any(Student.class))).thenReturn(student1);

        // Act
        Student result = studentService.saveStudent(student1);

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("2021001", result.getRoll());
        assertEquals("john@example.com", result.getEmail());
        verify(studentRepository, times(1)).save(student1);
    }

    @Test
    @DisplayName("Should delete student by id")
    void testDeleteStudent() {
        // Arrange
        doNothing().when(studentRepository).deleteById(1L);

        // Act
        studentService.deleteStudent(1L);

        // Assert
        verify(studentRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should find student by roll when exists")
    void testFindByRoll_WhenExists() {
        // Arrange
        when(studentRepository.findByRoll("2021001")).thenReturn(Optional.of(student1));

        // Act
        Optional<Student> result = studentService.findByRoll("2021001");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("2021001", result.get().getRoll());
        assertEquals("John Doe", result.get().getName());
        verify(studentRepository, times(1)).findByRoll("2021001");
    }

    @Test
    @DisplayName("Should return empty when roll not found")
    void testFindByRoll_WhenNotExists() {
        // Arrange
        when(studentRepository.findByRoll("9999999")).thenReturn(Optional.empty());

        // Act
        Optional<Student> result = studentService.findByRoll("9999999");

        // Assert
        assertFalse(result.isPresent());
        verify(studentRepository, times(1)).findByRoll("9999999");
    }

    @Test
    @DisplayName("Should handle empty list when no students exist")
    void testGetAllStudents_WhenEmpty() {
        // Arrange
        when(studentRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Student> result = studentService.getAllStudents();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should update existing student")
    void testUpdateStudent() {
        // Arrange
        Student existingStudent = new Student();
        existingStudent.setId(1L);
        existingStudent.setName("John Doe");
        existingStudent.setRoll("2021001");
        existingStudent.setEmail("john@example.com");

        Student updatedStudent = new Student();
        updatedStudent.setId(1L);
        updatedStudent.setName("John Updated");
        updatedStudent.setRoll("2021001");
        updatedStudent.setEmail("john.updated@example.com");

        when(studentRepository.save(any(Student.class))).thenReturn(updatedStudent);

        // Act
        Student result = studentService.saveStudent(updatedStudent);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Updated", result.getName());
        assertEquals("john.updated@example.com", result.getEmail());
        verify(studentRepository, times(1)).save(updatedStudent);
    }
}
