package com.example.aptease.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.aptease.entity.Role;
import com.example.aptease.entity.User;
import com.example.aptease.entity.DeliveryScore;
import com.example.aptease.service.UserService;
import com.example.aptease.service.DeliveryScoreService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final DeliveryScoreService deliveryScoreService;

    public AdminController(UserService userService, DeliveryScoreService deliveryScoreService) {
        this.userService = userService;
        this.deliveryScoreService = deliveryScoreService;
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";  // Updated path
    }

    @GetMapping("/residents")
    public String viewResidents(Model model) {
        model.addAttribute("residents", userService.getAllResidents());
        return "admin/residents";  // Updated path
    }

    @GetMapping("/delivery-agents")
    public String viewDeliveryAgents(Model model) {
        List<User> agents = userService.getAllDeliveryAgents();
        List<DeliveryScore> scores = agents.stream()
            .map(agent -> deliveryScoreService.getScore(agent))
            .collect(Collectors.toList());

        model.addAttribute("agents", agents);
        model.addAttribute("scores", scores);
        return "admin/delivery-agents";  // Updated path
    }

    @PostMapping("/user/save")
    public String saveUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            if (user.getId() == null) {
                userService.saveUser(user);
                redirectAttributes.addFlashAttribute("success", "User created successfully!");
            } else {
                userService.updateUser(user);
                redirectAttributes.addFlashAttribute("success", "User updated successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error processing user: " + e.getMessage());
        }
        
        return user.getRole() == Role.RESIDENT ? 
               "redirect:/admin/residents" : 
               "redirect:/admin/delivery-agents";
    }

    @PostMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/dashboard";  // Fixed redirect
    }
}
