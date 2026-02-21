package com.example.webapp.controller;

import com.example.webapp.dto.StudentDTO;
import com.example.webapp.entity.Dept;
import com.example.webapp.entity.Student;
import com.example.webapp.service.DeptService;
import com.example.webapp.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    
    @Autowired
    private DeptService deptService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public String getStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "students";
    }

    @GetMapping("/add")
    public String addStudent(Model model) {
        model.addAttribute("student", new StudentDTO());
        model.addAttribute("departments", deptService.getAllDepartments());
        return "student-form";
    }

    @PostMapping("/store")
    public String storeStudent(@ModelAttribute("student") StudentDTO studentDTO, 
                              @RequestParam(required = false) Long deptId,
                              Model model) {
        Student student = new Student();
        student.setName(studentDTO.getName());
        student.setRoll(studentDTO.getRoll());
        student.setEmail(studentDTO.getEmail());
        
        if (deptId != null) {
            Dept dept = deptService.getDepartmentById(deptId)
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            student.setDept(dept);
        }
        
        studentService.saveStudent(student);
        return "redirect:/students";
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "redirect:/students";
    }

    // REST API endpoints
    @GetMapping("/api")
    @ResponseBody
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}


