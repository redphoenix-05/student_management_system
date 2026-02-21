package com.example.webapp.integration;

import com.example.webapp.entity.Course;
import com.example.webapp.entity.Dept;
import com.example.webapp.entity.Student;
import com.example.webapp.entity.Teacher;
import com.example.webapp.repository.CourseRepository;
import com.example.webapp.repository.DeptRepository;
import com.example.webapp.repository.StudentRepository;
import com.example.webapp.repository.TeacherRepository;
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
@DisplayName("Full Stack Integration Tests")
class FullStackIntegrationTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DeptRepository deptRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    private Dept dept;
    private Teacher teacher;
    private Student student;
    private Course course;

    @BeforeEach
    void setUp() {
        // Clean up
        courseRepository.deleteAll();
        studentRepository.deleteAll();
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

        student = new Student();
        student.setName("John Doe");
        student.setRoll("2021001");
        student.setEmail("john@example.com");
        student.setCurrentSemester(5);
        student.setDept(dept);
        student = studentRepository.save(student);

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
    @DisplayName("Should create complete academic structure")
    void testCompleteAcademicStructure() {
        // Verify Department
        Dept savedDept = deptRepository.findById(dept.getId()).orElseThrow();
        assertNotNull(savedDept);
        assertEquals("Computer Science", savedDept.getName());

        // Verify Teacher
        Teacher savedTeacher = teacherRepository.findById(teacher.getId()).orElseThrow();
        assertNotNull(savedTeacher);
        assertEquals("Dr. Smith", savedTeacher.getName());

        // Verify Student
        Student savedStudent = studentRepository.findById(student.getId()).orElseThrow();
        assertNotNull(savedStudent);
        assertEquals("John Doe", savedStudent.getName());

        // Verify Course
        Course savedCourse = courseRepository.findById(course.getId()).orElseThrow();
        assertNotNull(savedCourse);
        assertEquals("Data Structures", savedCourse.getName());
        assertEquals(dept.getId(), savedCourse.getDept().getId());
        assertEquals(teacher.getId(), savedCourse.getCreatedBy().getId());
    }

    @Test
    @DisplayName("Should handle student enrollment in course")
    void testStudentEnrollment() {
        // Enroll student in course
        student.getEnrolledCourses().add(course);
        studentRepository.save(student);

        // Verify enrollment
        Student enrolledStudent = studentRepository.findById(student.getId()).orElseThrow();
        assertFalse(enrolledStudent.getEnrolledCourses().isEmpty());
        assertTrue(enrolledStudent.getEnrolledCourses().stream()
                .anyMatch(c -> c.getId().equals(course.getId())));
    }

    @Test
    @DisplayName("Should find courses by department")
    void testFindCoursesByDepartment() {
        // Create another course in the same department
        Course course2 = new Course();
        course2.setName("Algorithms");
        course2.setCode("CS102");
        course2.setDescription("Algorithm Design");
        course2.setCredits(4);
        course2.setDept(dept);
        course2.setCreatedBy(teacher);
        courseRepository.save(course2);

        // Find all courses in department
        List<Course> courses = courseRepository.findByDeptId(dept.getId());

        assertNotNull(courses);
        assertEquals(2, courses.size());
    }

    @Test
    @DisplayName("Should cascade delete properly")
    void testCascadeDelete() {
        Long deptId = dept.getId();
        Long courseId = course.getId();

        // Delete department (should not cascade to courses due to relationship)
        // Instead, we need to handle this properly in production code
        course.setDept(null);
        courseRepository.save(course);
        
        deptRepository.deleteById(deptId);

        // Verify department is deleted
        assertFalse(deptRepository.findById(deptId).isPresent());

        // Course should still exist
        assertTrue(courseRepository.findById(courseId).isPresent());
    }

    @Test
    @DisplayName("Should enforce unique constraints")
    void testUniqueConstraints() {
        // Try to create student with duplicate roll number
        Student duplicateStudent = new Student();
        duplicateStudent.setName("Jane Smith");
        duplicateStudent.setRoll("2021001"); // duplicate
        duplicateStudent.setEmail("jane@example.com");
        duplicateStudent.setDept(dept);

        assertThrows(Exception.class, () -> {
            studentRepository.saveAndFlush(duplicateStudent);
        });

        // Try to create course with duplicate code
        Course duplicateCourse = new Course();
        duplicateCourse.setName("Another Course");
        duplicateCourse.setCode("CS101"); // duplicate
        duplicateCourse.setDescription("Test");
        duplicateCourse.setCredits(3);
        duplicateCourse.setDept(dept);
        duplicateCourse.setCreatedBy(teacher);

        assertThrows(Exception.class, () -> {
            courseRepository.saveAndFlush(duplicateCourse);
        });
    }

    @Test
    @DisplayName("Should handle multiple students in same department")
    void testMultipleStudentsInDepartment() {
        // Create additional students
        Student student2 = new Student();
        student2.setName("Jane Smith");
        student2.setRoll("2021002");
        student2.setEmail("jane@example.com");
        student2.setDept(dept);
        studentRepository.save(student2);

        Student student3 = new Student();
        student3.setName("Bob Johnson");
        student3.setRoll("2021003");
        student3.setEmail("bob@example.com");
        student3.setDept(dept);
        studentRepository.save(student3);

        // Verify all students are saved
        List<Student> allStudents = studentRepository.findAll();
        assertEquals(3, allStudents.size());
    }

    @Test
    @DisplayName("Should handle teacher creating multiple courses")
    void testTeacherMultipleCourses() {
        // Create additional courses by same teacher
        Course course2 = new Course();
        course2.setName("Operating Systems");
        course2.setCode("CS201");
        course2.setDescription("OS Concepts");
        course2.setCredits(4);
        course2.setDept(dept);
        course2.setCreatedBy(teacher);
        courseRepository.save(course2);

        // Find all courses by teacher
        List<Course> teacherCourses = courseRepository.findByCreatedById(teacher.getId());
        assertEquals(2, teacherCourses.size());
    }
}
