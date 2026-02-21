package com.example.webapp.controller;

import com.example.webapp.entity.Course;
import com.example.webapp.entity.Dept;
import com.example.webapp.entity.Teacher;
import com.example.webapp.repository.CourseRepository;
import com.example.webapp.repository.DeptRepository;
import com.example.webapp.repository.TeacherRepository;
import com.example.webapp.service.CourseService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Course Controller Integration Tests")
class CourseControllerIntegrationTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DeptRepository deptRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    private Dept dept;
    private Teacher teacher;
    private Course course;

    @BeforeEach
    void setUp() {
        // Clean up
        courseRepository.deleteAll();
        teacherRepository.deleteAll();
        deptRepository.deleteAll();

        // Setup test data
        dept = new Dept();
        dept.setName("Computer Science");
        dept = deptRepository.save(dept);

        teacher = new Teacher();
        teacher.setName("Dr. Smith");
        teacher.setEmail("smith@example.com");
        teacher.setDept(dept);
        teacher = teacherRepository.save(teacher);

        course = new Course();
        course.setName("Data Structures");
        course.setCode("CS101");
        course.setDescription("Introduction to Data Structures");
        course.setCredits(3);
        course.setDept(dept);
        course.setCreatedBy(teacher);
        course = courseRepository.save(course);
    }

    @Test
    @DisplayName("Should retrieve all courses via service")
    void testGetAllCourses() {
        // Act
        List<Course> courses = courseService.getAllCourses();

        // Assert
        assertNotNull(courses);
        assertFalse(courses.isEmpty());
        assertTrue(courses.stream().anyMatch(c -> c.getName().equals("Data Structures")));
    }

    @Test
    @DisplayName("Should retrieve course by id via service")
    void testGetCourseById() {
        // Act
        var foundCourse = courseService.getCourseById(course.getId());

        // Assert
        assertTrue(foundCourse.isPresent());
        assertEquals("Data Structures", foundCourse.get().getName());
        assertEquals("CS101", foundCourse.get().getCode());
    }

    @Test
    @DisplayName("Should return empty for non-existent course")
    void testGetCourseById_NotFound() {
        // Act
        var foundCourse = courseService.getCourseById(99999L);

        // Assert
        assertFalse(foundCourse.isPresent());
    }

    @Test
    @DisplayName("Should create new course")
    void testSaveCourse() {
        // Arrange
        long initialCount = courseRepository.count();

        // Create a new course
        Course newCourse = new Course();
        newCourse.setName("Algorithms");
        newCourse.setCode("CS102");
        newCourse.setDescription("Algorithm Design");
        newCourse.setCredits(4);
        newCourse.setDept(dept);
        newCourse.setCreatedBy(teacher);

        // Act
        Course savedCourse = courseRepository.save(newCourse);

        // Assert
        assertNotNull(savedCourse.getId());
        assertEquals(initialCount + 1, courseRepository.count());
        assertEquals("Algorithms", savedCourse.getName());
    }

    @Test
    @DisplayName("Should find courses by department")
    void testFindCoursesByDepartment() {
        // Act
        List<Course> courses = courseRepository.findByDeptId(dept.getId());

        // Assert
        assertNotNull(courses);
        assertFalse(courses.isEmpty());
        assertTrue(courses.stream().allMatch(c -> c.getDept().getId().equals(dept.getId())));
    }

    @Test
    @DisplayName("Should find courses by teacher")
    void testFindCoursesByTeacher() {
        // Act
        List<Course> courses = courseRepository.findByCreatedById(teacher.getId());

        // Assert
        assertNotNull(courses);
        assertFalse(courses.isEmpty());
        assertTrue(courses.stream().allMatch(c -> c.getCreatedBy().getId().equals(teacher.getId())));
    }

    @Test
    @DisplayName("Should find course by code")
    void testFindCourseByCode() {
        // Act
        var foundCourse = courseRepository.findByCode("CS101");

        // Assert
        assertTrue(foundCourse.isPresent());
        assertEquals("Data Structures", foundCourse.get().getName());
    }

    @Test
    @DisplayName("Should delete course")
    void testDeleteCourse() {
        // Arrange
        Long courseId = course.getId();
        long initialCount = courseRepository.count();

        // Act
        courseRepository.deleteById(courseId);

        // Assert
        assertEquals(initialCount - 1, courseRepository.count());
        assertFalse(courseRepository.findById(courseId).isPresent());
    }
}
