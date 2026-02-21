package com.example.webapp.repository;

import com.example.webapp.entity.Course;
import com.example.webapp.entity.Dept;
import com.example.webapp.entity.Teacher;
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
@DisplayName("Course Repository Integration Tests")
class CourseRepositoryIntegrationTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DeptRepository deptRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    private Dept dept;
    private Teacher teacher;
    private Course course1;
    private Course course2;

    @BeforeEach
    void setUp() {
        // Clean up
        courseRepository.deleteAll();
        teacherRepository.deleteAll();
        deptRepository.deleteAll();

        // Create and save department
        dept = new Dept();
        dept.setName("Computer Science");
        dept = deptRepository.save(dept);

        // Create and save teacher
        teacher = new Teacher();
        teacher.setName("Dr. Smith");
        teacher.setEmail("smith@example.com");
        teacher.setDept(dept);
        teacher = teacherRepository.save(teacher);

        // Create courses
        course1 = new Course();
        course1.setName("Data Structures");
        course1.setCode("CS101");
        course1.setDescription("Introduction to Data Structures");
        course1.setCredits(3);
        course1.setDept(dept);
        course1.setCreatedBy(teacher);
        course1 = courseRepository.save(course1);

        course2 = new Course();
        course2.setName("Algorithms");
        course2.setCode("CS102");
        course2.setDescription("Algorithm Design");
        course2.setCredits(4);
        course2.setDept(dept);
        course2.setCreatedBy(teacher);
        course2 = courseRepository.save(course2);
    }

    @Test
    @DisplayName("Should find all courses")
    void testFindAll() {
        // Act
        List<Course> courses = courseRepository.findAll();

        // Assert
        assertNotNull(courses);
        assertTrue(courses.size() >= 2);
    }

    @Test
    @DisplayName("Should find course by id")
    void testFindById() {
        // Act
        Optional<Course> found = courseRepository.findById(course1.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Data Structures", found.get().getName());
        assertEquals("CS101", found.get().getCode());
    }

    @Test
    @DisplayName("Should find course by code")
    void testFindByCode() {
        // Act
        Optional<Course> found = courseRepository.findByCode("CS101");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Data Structures", found.get().getName());
        assertEquals(3, found.get().getCredits());
    }

    @Test
    @DisplayName("Should return empty when code not found")
    void testFindByCode_NotFound() {
        // Act
        Optional<Course> found = courseRepository.findByCode("CS999");

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should find courses by department")
    void testFindByDeptId() {
        // Act
        List<Course> courses = courseRepository.findByDeptId(dept.getId());

        // Assert
        assertNotNull(courses);
        assertTrue(courses.size() >= 2);
        courses.forEach(course -> assertEquals(dept.getId(), course.getDept().getId()));
    }

    @Test
    @DisplayName("Should find courses by teacher")
    void testFindByCreatedById() {
        // Act
        List<Course> courses = courseRepository.findByCreatedById(teacher.getId());

        // Assert
        assertNotNull(courses);
        assertTrue(courses.size() >= 2);
        courses.forEach(course -> assertEquals(teacher.getId(), course.getCreatedBy().getId()));
    }

    @Test
    @DisplayName("Should save new course")
    void testSave() {
        // Arrange
        Course newCourse = new Course();
        newCourse.setName("Database Systems");
        newCourse.setCode("CS201");
        newCourse.setDescription("Introduction to Databases");
        newCourse.setCredits(3);
        newCourse.setDept(dept);
        newCourse.setCreatedBy(teacher);

        // Act
        Course saved = courseRepository.save(newCourse);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("Database Systems", saved.getName());
        assertEquals("CS201", saved.getCode());
    }

    @Test
    @DisplayName("Should update existing course")
    void testUpdate() {
        // Arrange
        Course existingCourse = courseRepository.findById(course1.getId()).orElseThrow();
        existingCourse.setName("Advanced Data Structures");
        existingCourse.setCredits(4);

        // Act
        Course updated = courseRepository.save(existingCourse);

        // Assert
        assertEquals("Advanced Data Structures", updated.getName());
        assertEquals(4, updated.getCredits());
        assertEquals("CS101", updated.getCode()); // unchanged
    }

    @Test
    @DisplayName("Should delete course by id")
    void testDeleteById() {
        // Arrange
        Long courseId = course1.getId();

        // Act
        courseRepository.deleteById(courseId);

        // Assert
        Optional<Course> deleted = courseRepository.findById(courseId);
        assertFalse(deleted.isPresent());
    }

    @Test
    @DisplayName("Should return empty list when no courses for department")
    void testFindByDeptId_NoCourses() {
        // Arrange
        Dept newDept = new Dept();
        newDept.setName("Mathematics");
        newDept = deptRepository.save(newDept);

        // Act
        List<Course> courses = courseRepository.findByDeptId(newDept.getId());

        // Assert
        assertNotNull(courses);
        assertTrue(courses.isEmpty());
    }

    @Test
    @DisplayName("Should enforce unique constraint on course code")
    void testUniqueCodeConstraint() {
        // Arrange
        Course duplicateCourse = new Course();
        duplicateCourse.setName("Another Course");
        duplicateCourse.setCode("CS101"); // duplicate code
        duplicateCourse.setDescription("Test");
        duplicateCourse.setCredits(3);
        duplicateCourse.setDept(dept);
        duplicateCourse.setCreatedBy(teacher);

        // Act & Assert
        assertThrows(Exception.class, () -> {
            courseRepository.saveAndFlush(duplicateCourse);
        });
    }

    @Test
    @DisplayName("Should count courses correctly")
    void testCount() {
        // Act
        long count = courseRepository.count();

        // Assert
        assertTrue(count >= 2);
    }
}
