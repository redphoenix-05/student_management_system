package com.example.webapp.controller;

import com.example.webapp.entity.Dept;
import com.example.webapp.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/departments")
public class DeptController {

    @Autowired
    private DeptService deptService;

    @GetMapping
    public String listDepartments(Model model) {
        List<Dept> departments = deptService.getAllDepartments();
        model.addAttribute("departments", departments);
        return "departments";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("dept", new Dept());
        return "dept-form";
    }

    @PostMapping
    public String saveDepartment(@ModelAttribute Dept dept) {
        deptService.saveDepartment(dept);
        return "redirect:/departments";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Dept dept = deptService.getDepartmentById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        model.addAttribute("dept", dept);
        return "dept-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteDepartment(@PathVariable Long id) {
        deptService.deleteDepartment(id);
        return "redirect:/departments";
    }

    // REST API endpoints
    @GetMapping("/api")
    @ResponseBody
    public List<Dept> getAllDepartments() {
        return deptService.getAllDepartments();
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Dept> getDepartmentById(@PathVariable Long id) {
        return deptService.getDepartmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
