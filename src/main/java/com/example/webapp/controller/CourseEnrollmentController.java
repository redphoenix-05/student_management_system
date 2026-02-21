package com.example.webapp.controller;

import com.example.webapp.entity.Course;
import com.example.webapp.entity.Student;
import com.example.webapp.entity.User;
import com.example.webapp.service.CourseService;
import com.example.webapp.service.StudentService;
import com.example.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/courses")
public class CourseEnrollmentController {

    @Autowired
    private UserService userService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private CourseService courseService;

    @GetMapping("/my-courses")
    public String viewMyCourses(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username).orElse(null);
        
        if (user == null || user.getStudent() == null) {
            return "redirect:/home";
        }

        Student student = user.getStudent();
        model.addAttribute("student", student);
        model.addAttribute("courses", student.getEnrolledCourses());
        return "my-courses";
    }

    @GetMapping("/available")
    public String viewAvailableCourses(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username).orElse(null);
        
        if (user == null || user.getStudent() == null) {
            return "redirect:/home";
        }

        Student student = user.getStudent();
        List<Course> allCourses = courseService.getAllCourses();
        
        // Filter out courses already enrolled
        List<Course> availableCourses = allCourses.stream()
            .filter(course -> !student.getEnrolledCourses().contains(course))
            .toList();
        
        model.addAttribute("student", student);
        model.addAttribute("availableCourses", availableCourses);
        return "available-courses";
    }

    @PostMapping("/enroll/{courseId}")
    public String enrollInCourse(@PathVariable Long courseId, RedirectAttributes redirectAttributes) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username).orElse(null);
        
        if (user == null || user.getStudent() == null) {
            return "redirect:/home";
        }

        try {
            Student student = user.getStudent();
            Course course = courseService.getCourseById(courseId).orElse(null);
            
            if (course == null) {
                redirectAttributes.addFlashAttribute("error", "Course not found");
                return "redirect:/courses/available";
            }
            
            if (!student.getEnrolledCourses().contains(course)) {
                student.getEnrolledCourses().add(course);
                studentService.saveStudent(student);
                redirectAttributes.addFlashAttribute("success", "Successfully enrolled in " + course.getName());
            } else {
                redirectAttributes.addFlashAttribute("error", "You are already enrolled in this course");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to enroll in course: " + e.getMessage());
        }
        
        return "redirect:/courses/available";
    }

    @PostMapping("/unenroll/{courseId}")
    public String unenrollFromCourse(@PathVariable Long courseId, RedirectAttributes redirectAttributes) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username).orElse(null);
        
        if (user == null || user.getStudent() == null) {
            return "redirect:/home";
        }

        try {
            Student student = user.getStudent();
            Course course = courseService.getCourseById(courseId).orElse(null);
            
            if (course == null) {
                redirectAttributes.addFlashAttribute("error", "Course not found");
                return "redirect:/courses/my-courses";
            }
            
            if (student.getEnrolledCourses().contains(course)) {
                student.getEnrolledCourses().remove(course);
                studentService.saveStudent(student);
                redirectAttributes.addFlashAttribute("success", "Successfully unenrolled from " + course.getName());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to unenroll from course: " + e.getMessage());
        }
        
        return "redirect:/courses/my-courses";
    }
}
