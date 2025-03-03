package com.example.aptease.controller;

import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.example.aptease.entity.*;
import com.example.aptease.service.*;
import java.util.List;

@Controller
@RequestMapping("/delivery")
public class DeliveryAgentController {
    private final UserService userService;
    private final DeliveryScoreService scoreService;
    private final ServiceBookingService bookingService;

    public DeliveryAgentController(UserService userService, DeliveryScoreService scoreService, ServiceBookingService bookingService) {
        this.userService = userService;
        this.scoreService = scoreService;
        this.bookingService = bookingService;
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        User deliveryAgent = userService.findByUsername(username);
        DeliveryScore score = scoreService.getScore(deliveryAgent);
        List<ServiceBooking> orders = bookingService.getPendingBookingsForAgent(deliveryAgent);
        
        model.addAttribute("deliveryAgent", deliveryAgent);
        model.addAttribute("score", score);
        model.addAttribute("orders", orders); // Updated to match Thymeleaf variable

        return "delivery/dashboard";
    }

    @PostMapping("/start")
    public ResponseEntity<String> startDelivery(@RequestParam Long orderId) {
        bookingService.updateToInProgress(orderId);
        return ResponseEntity.ok("Delivery started successfully");
    }
}
