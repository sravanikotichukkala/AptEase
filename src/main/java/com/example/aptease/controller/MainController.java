package com.example.aptease.controller;

import com.example.aptease.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MainController {
    private final UserService userService;

    public MainController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String index()
    {
    	return "index";
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        switch (role) {
            case "ROLE_ADMIN":
                return "redirect:/admin/dashboard";
            case "ROLE_RESIDENT":
                return "redirect:/resident/dashboard";
            case "ROLE_DELIVERY_AGENT":
                return "redirect:/delivery/dashboard";
            default:
                return "redirect:/login";
        }
    }
}
