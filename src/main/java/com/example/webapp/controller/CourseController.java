package com.example.webapp.controller;

import com.example.webapp.entity.Course;
import com.example.webapp.entity.Dept;
import com.example.webapp.entity.Teacher;
import com.example.webapp.service.CourseService;
import com.example.webapp.service.DeptService;
import com.example.webapp.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private DeptService deptService;

    @Autowired
    private TeacherService teacherService;

    @GetMapping
    public String listCourses(Model model) {
        List<Course> courses = courseService.getAllCourses();
        model.addAttribute("courses", courses);
        return "courses";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("departments", deptService.getAllDepartments());
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "course-form";
    }

    @PostMapping
    public String saveCourse(@ModelAttribute Course course, 
                           @RequestParam(required = false) Long deptId,
                           @RequestParam(required = false) Long teacherId) {
        if (deptId != null) {
            Dept dept = deptService.getDepartmentById(deptId)
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            course.setDept(dept);
        }
        if (teacherId != null) {
            Teacher teacher = teacherService.getTeacherById(teacherId)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));
            course.setCreatedBy(teacher);
        }
        courseService.saveCourse(course);
        return "redirect:/courses";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Course course = courseService.getCourseById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        model.addAttribute("course", course);
        model.addAttribute("departments", deptService.getAllDepartments());
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "course-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return "redirect:/courses";
    }

    // REST API endpoints
    @GetMapping("/api")
    @ResponseBody
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
