package com.example.webapp.controller;

import com.example.webapp.entity.Student;
import com.example.webapp.entity.Teacher;
import com.example.webapp.entity.User;
import com.example.webapp.service.StudentService;
import com.example.webapp.service.TeacherService;
import com.example.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private TeacherService teacherService;

    @GetMapping
    public String viewProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            model.addAttribute("user", user);

            if (user.getStudent() != null) {
                model.addAttribute("student", user.getStudent());
                model.addAttribute("userType", "STUDENT");
            } else if (user.getTeacher() != null) {
                model.addAttribute("teacher", user.getTeacher());
                model.addAttribute("userType", "TEACHER");
            }
        }

        return "profile";
    }

    @GetMapping("/edit")
    public String editProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            model.addAttribute("user", user);

            if (user.getStudent() != null) {
                model.addAttribute("student", user.getStudent());
                model.addAttribute("userType", "STUDENT");
            } else if (user.getTeacher() != null) {
                model.addAttribute("teacher", user.getTeacher());
                model.addAttribute("userType", "TEACHER");
            }
        }

        return "profile-edit";
    }

    @PostMapping("/update")
    public String updateProfile(@RequestParam(required = false) String name,
                               @RequestParam(required = false) String email,
                               @RequestParam(required = false) String phone,
                               @RequestParam(required = false) Integer currentSemester,
                               @RequestParam(required = false) String academicYear,
                               RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (user.getStudent() != null) {
                Student student = user.getStudent();
                if (name != null) student.setName(name);
                if (email != null) student.setEmail(email);
                if (currentSemester != null) student.setCurrentSemester(currentSemester);
                if (academicYear != null) student.setAcademicYear(academicYear);
                studentService.saveStudent(student);
            } else if (user.getTeacher() != null) {
                Teacher teacher = user.getTeacher();
                if (name != null) teacher.setName(name);
                if (email != null) teacher.setEmail(email);
                if (phone != null) teacher.setPhone(phone);
                teacherService.saveTeacher(teacher);
            }

            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "User not found!");
        }

        return "redirect:/profile";
    }
}
