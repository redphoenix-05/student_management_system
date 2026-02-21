package com.example.webapp.controller;

import com.example.webapp.entity.Dept;
import com.example.webapp.entity.Teacher;
import com.example.webapp.service.DeptService;
import com.example.webapp.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/teachers")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private DeptService deptService;

    @GetMapping
    public String listTeachers(Model model) {
        List<Teacher> teachers = teacherService.getAllTeachers();
        model.addAttribute("teachers", teachers);
        return "teachers";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("teacher", new Teacher());
        model.addAttribute("departments", deptService.getAllDepartments());
        return "teacher-form";
    }

    @PostMapping
    public String saveTeacher(@ModelAttribute Teacher teacher, @RequestParam(required = false) Long deptId) {
        if (deptId != null) {
            Dept dept = deptService.getDepartmentById(deptId)
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            teacher.setDept(dept);
        }
        teacherService.saveTeacher(teacher);
        return "redirect:/teachers";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Teacher teacher = teacherService.getTeacherById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        model.addAttribute("teacher", teacher);
        model.addAttribute("departments", deptService.getAllDepartments());
        return "teacher-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return "redirect:/teachers";
    }

    // REST API endpoints
    @GetMapping("/api")
    @ResponseBody
    public List<Teacher> getAllTeachers() {
        return teacherService.getAllTeachers();
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Teacher> getTeacherById(@PathVariable Long id) {
        return teacherService.getTeacherById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
