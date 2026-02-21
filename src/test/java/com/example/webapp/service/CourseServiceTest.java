package com.example.webapp.service;

import com.example.webapp.entity.Course;
import com.example.webapp.entity.Dept;
import com.example.webapp.entity.Teacher;
import com.example.webapp.repository.CourseRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Course Service Unit Tests")
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    private Course course1;
    private Course course2;
    private Dept dept;
    private Teacher teacher;

    @BeforeEach
    void setUp() {
        dept = new Dept();
        dept.setId(1L);
        dept.setName("Computer Science");

        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setName("Dr. Smith");

        course1 = new Course();
        course1.setId(1L);
        course1.setName("Data Structures");
        course1.setCode("CS101");
        course1.setDescription("Introduction to Data Structures");
        course1.setCredits(3);
        course1.setDept(dept);
        course1.setCreatedBy(teacher);

        course2 = new Course();
        course2.setId(2L);
        course2.setName("Algorithms");
        course2.setCode("CS102");
        course2.setDescription("Algorithm Design and Analysis");
        course2.setCredits(4);
        course2.setDept(dept);
        course2.setCreatedBy(teacher);
    }

    @Test
    @DisplayName("Should retrieve all courses")
    void testGetAllCourses() {
        // Arrange
        when(courseRepository.findAll()).thenReturn(Arrays.asList(course1, course2));

        // Act
        List<Course> result = courseService.getAllCourses();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Data Structures", result.get(0).getName());
        assertEquals("Algorithms", result.get(1).getName());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve course by id when exists")
    void testGetCourseById_WhenExists() {
        // Arrange
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course1));

        // Act
        Optional<Course> result = courseService.getCourseById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Data Structures", result.get().getName());
        assertEquals("CS101", result.get().getCode());
        verify(courseRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when course not found")
    void testGetCourseById_WhenNotExists() {
        // Arrange
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Course> result = courseService.getCourseById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(courseRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should save course successfully")
    void testSaveCourse() {
        // Arrange
        when(courseRepository.save(any(Course.class))).thenReturn(course1);

        // Act
        Course result = courseService.saveCourse(course1);

        // Assert
        assertNotNull(result);
        assertEquals("Data Structures", result.getName());
        assertEquals("CS101", result.getCode());
        verify(courseRepository, times(1)).save(course1);
    }

    @Test
    @DisplayName("Should delete course by id")
    void testDeleteCourse() {
        // Arrange
        doNothing().when(courseRepository).deleteById(1L);

        // Act
        courseService.deleteCourse(1L);

        // Assert
        verify(courseRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should retrieve courses by department")
    void testGetCoursesByDept() {
        // Arrange
        when(courseRepository.findByDeptId(1L)).thenReturn(Arrays.asList(course1, course2));

        // Act
        List<Course> result = courseService.getCoursesByDept(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        result.forEach(course -> assertEquals(1L, course.getDept().getId()));
        verify(courseRepository, times(1)).findByDeptId(1L);
    }

    @Test
    @DisplayName("Should retrieve courses by teacher")
    void testGetCoursesByTeacher() {
        // Arrange
        when(courseRepository.findByCreatedById(1L)).thenReturn(Arrays.asList(course1, course2));

        // Act
        List<Course> result = courseService.getCoursesByTeacher(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        result.forEach(course -> assertEquals(1L, course.getCreatedBy().getId()));
        verify(courseRepository, times(1)).findByCreatedById(1L);
    }

    @Test
    @DisplayName("Should find course by code when exists")
    void testFindByCode_WhenExists() {
        // Arrange
        when(courseRepository.findByCode("CS101")).thenReturn(Optional.of(course1));

        // Act
        Optional<Course> result = courseService.findByCode("CS101");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("CS101", result.get().getCode());
        assertEquals("Data Structures", result.get().getName());
        verify(courseRepository, times(1)).findByCode("CS101");
    }

    @Test
    @DisplayName("Should return empty when course code not found")
    void testFindByCode_WhenNotExists() {
        // Arrange
        when(courseRepository.findByCode("CS999")).thenReturn(Optional.empty());

        // Act
        Optional<Course> result = courseService.findByCode("CS999");

        // Assert
        assertFalse(result.isPresent());
        verify(courseRepository, times(1)).findByCode("CS999");
    }

    @Test
    @DisplayName("Should handle empty list when no courses exist")
    void testGetAllCourses_WhenEmpty() {
        // Arrange
        when(courseRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Course> result = courseService.getAllCourses();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(courseRepository, times(1)).findAll();
    }
}
