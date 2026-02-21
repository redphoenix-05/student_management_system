package com.example.webapp.controller;

import com.example.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "logout", required = false) String logout,
                       Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password!");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }
        return "login";
    }

    @GetMapping("/signup")
    public String showSignupForm() {
        return "signup";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String role,
                              @RequestParam String username,
                              @RequestParam String email,
                              @RequestParam String password,
                              @RequestParam String confirmPassword,
                              @RequestParam(required = false) String name,
                              @RequestParam(required = false) String roll,
                              @RequestParam(required = false) String phone,
                              RedirectAttributes redirectAttributes) {
        
        // Validation
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match!");
            return "redirect:/signup";
        }

        if (userService.existsByUsername(username)) {
            redirectAttributes.addFlashAttribute("error", "Username already exists!");
            return "redirect:/signup";
        }

        if (userService.existsByEmail(email)) {
            redirectAttributes.addFlashAttribute("error", "Email already exists!");
            return "redirect:/signup";
        }

        try {
            if ("STUDENT".equals(role)) {
                if (name == null || name.isEmpty() || roll == null || roll.isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "Name and Roll are required for students!");
                    return "redirect:/signup";
                }
                userService.registerStudent(username, email, password, name, roll);
            } else if ("TEACHER".equals(role)) {
                if (name == null || name.isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "Name is required for teachers!");
                    return "redirect:/signup";
                }
                userService.registerTeacher(username, email, password, name, phone);
            } else {
                redirectAttributes.addFlashAttribute("error", "Invalid role selected!");
                return "redirect:/signup";
            }

            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration failed: " + e.getMessage());
            return "redirect:/signup";
        }
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, 
                                       RedirectAttributes redirectAttributes) {
        String token = userService.generateResetToken(email);
        
        if (token != null) {
            // In a real application, send email with reset link
            // For now, just display the token (for demo purposes)
            redirectAttributes.addFlashAttribute("success", 
                "Password reset link has been sent to your email. " +
                "For demo purposes, your reset token is: " + token);
            redirectAttributes.addFlashAttribute("resetToken", token);
        } else {
            redirectAttributes.addFlashAttribute("error", 
                "No account found with that email address.");
        }
        
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam String token, Model model) {
        if (userService.validateResetToken(token)) {
            model.addAttribute("token", token);
            return "reset-password";
        } else {
            model.addAttribute("error", "Invalid or expired reset token.");
            return "redirect:/login";
        }
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String token,
                                      @RequestParam String password,
                                      @RequestParam String confirmPassword,
                                      RedirectAttributes redirectAttributes) {
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match!");
            return "redirect:/reset-password?token=" + token;
        }

        if (userService.resetPassword(token, password)) {
            redirectAttributes.addFlashAttribute("success", 
                "Password has been reset successfully. Please login.");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("error", 
                "Failed to reset password. Token may have expired.");
            return "redirect:/login";
        }
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
