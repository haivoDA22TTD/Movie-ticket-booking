package com.cinema.controller;

import com.cinema.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }
    
    @PostMapping("/register")
    public String register(
        @RequestParam String email,
        @RequestParam String password,
        @RequestParam String fullName,
        @RequestParam String phone,
        Model model
    ) {
        try {
            userService.registerUser(email, password, fullName, phone);
            return "redirect:/login?registered";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}
