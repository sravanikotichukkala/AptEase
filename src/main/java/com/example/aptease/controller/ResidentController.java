package com.example.aptease.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.aptease.entity.AptService;
import com.example.aptease.entity.OrderItem;
import com.example.aptease.entity.ServiceBooking;
import com.example.aptease.entity.User;
import com.example.aptease.service.ServiceBookingService;
import com.example.aptease.service.UserService;

@Controller
@RequestMapping("/resident")
public class ResidentController {
    private final UserService userService;
    private final ServiceBookingService bookingService;

    public ResidentController(UserService userService, ServiceBookingService bookingService) {
        this.userService = userService;
        this.bookingService = bookingService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        User resident = userService.findByUsername(username);
        List<ServiceBooking> recentBookings = bookingService.getRecentBookings(resident, 5);
        
        // Get the delivery agent for this resident's flat
        User deliveryAgent = userService.getDeliveryAgentForResident(resident);
        
        model.addAttribute("resident", resident);
        model.addAttribute("deliveryAgent", deliveryAgent);
        model.addAttribute("recentBookings", recentBookings);
        return "resident/dashboard";
    }

    @GetMapping("/profile")
    public String viewProfile(Authentication authentication, Model model) {
        String username = authentication.getName();
        User resident = userService.findByUsername(username);
        // Get the delivery agent for this resident's flat
        User deliveryAgent = userService.getDeliveryAgentForResident(resident);
        
        model.addAttribute("resident", resident);
        model.addAttribute("deliveryAgent", deliveryAgent);
        return "resident/profile";
    }

    @GetMapping("/services")
    public String viewServices(Authentication authentication, Model model) {
        String username = authentication.getName();
        User resident = userService.findByUsername(username);
        model.addAttribute("resident", resident);
        model.addAttribute("services", AptService.values());
        return "resident/services";
    }

    @GetMapping("/book-service/{service}")
    public String bookServiceForm(@PathVariable("service") String service, Model model) {
        List<String> items;

        switch (service.toLowerCase()) {
            case "grocery":
                items = Arrays.asList("Rice", "Milk", "Vegetables", "Fruits", "Eggs", "Bread", "Cereal", "Coffee", "Tea", "Snacks");
                break;
            case "food":
                items = Arrays.asList("Pizza", "Burger", "Pasta", "Salad", "Sandwich", "Sushi", "Indian Food", "Chinese Food", "Mexican Food", "Desserts");
                break;
            case "laundry":
                items = Arrays.asList("Washing Clothes", "Dry Cleaning", "Ironing", "Folding", "Blanket Cleaning");
                break;
            case "water":
                items = Arrays.asList("Drinking Water (20L)", "Household Water Supply", "Emergency Water Tanker");
                break;
            case "pet_care":
                items = Arrays.asList("Pet Grooming", "Pet Sitting", "Veterinary Check-up", "Dog Walking", "Pet Training");
                break;
            case "maintenance":
                items = Arrays.asList("Plumbing", "Electrical Repair", "Carpentry", "Appliance Repair", "Home Cleaning");
                break;
            default:
                items = new ArrayList<>();  // No items for unknown services
        }

        model.addAttribute("serviceName", service);
        model.addAttribute("items", items);
        return "resident/book-service";
    }
    
    @PostMapping("/confirm-booking")
    public String confirmBooking(
            @RequestParam String serviceType,
            @RequestParam String itemName,
            @RequestParam Integer quantity,
            @RequestParam(required = false) String notes,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            User resident = userService.findByUsername(authentication.getName());
            
            OrderItem orderItem = new OrderItem();
            orderItem.setItemName(itemName);
            orderItem.setQuantity(quantity);
            
            List<OrderItem> items = Arrays.asList(orderItem);
            
            // Add logging here
            System.out.println("Service Type: " + serviceType);
            
            AptService aptService = AptService.valueOf(serviceType.toUpperCase());
            
            // Add logging here
            System.out.println("Created AptService enum: " + aptService);
            
            ServiceBooking booking = bookingService.createBooking(resident, aptService, items, notes);
            
            // Add logging here
            System.out.println("Booking created: " + booking);
            
            redirectAttributes.addFlashAttribute("successMessage", "Service booked successfully!");
            redirectAttributes.addFlashAttribute("booking", booking);
            redirectAttributes.addFlashAttribute("orderItem", orderItem);
            
            return "redirect:/resident/booking-success";
            
        } catch (Exception e) {
            // Log the full exception
            e.printStackTrace();
            
            // Add more detailed error message
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error booking service: " + e.getMessage() + " (" + e.getClass().getName() + ")");
            return "redirect:/resident/services";
        }
    }

    @PostMapping("/book-service")
    public String bookService(
            @RequestParam AptService aptService,
            @RequestParam String notes,
            @RequestParam(required = false) List<String> itemNames,
            @RequestParam(required = false) List<Integer> quantities,
            Authentication authentication) {
        
        User resident = userService.findByUsername(authentication.getName());
        
        List<OrderItem> items = null;
        if (itemNames != null && quantities != null) {
            items = new ArrayList<>();
            for (int i = 0; i < itemNames.size(); i++) {
                OrderItem item = new OrderItem();
                item.setItemName(itemNames.get(i));
                item.setQuantity(quantities.get(i));
                items.add(item);
            }
        }
        
        bookingService.createBooking(resident, aptService, items, notes);
        return "redirect:/resident/booking-success";
    }

    @GetMapping("/booking-success")
    public String bookingSuccess(Model model) {
        // If there's no booking in the flash attributes, redirect to services
        if (!model.containsAttribute("booking")) {
            return "redirect:/resident/services";
        }
        return "resident/booking-success";
    }
    @GetMapping("/booking-history")
    public String viewBookingHistory(Authentication authentication, Model model) {
        User resident = userService.findByUsername(authentication.getName());
        
        // ✅ Fetch updated bookings from DB
        List<ServiceBooking> bookings = bookingService.getResidentBookingHistory(resident);
        
        model.addAttribute("resident", resident);
        model.addAttribute("bookings", bookings);

        return "resident/booking-history";
    }



    @PostMapping("/rate-service")
    public String rateService(@RequestParam Long bookingId,
                            @RequestParam Integer rating,
                            RedirectAttributes redirectAttributes) {
        try {
            bookingService.addRatingAndComplete(bookingId, rating);
            redirectAttributes.addFlashAttribute("successMessage", "Rating submitted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error submitting rating: " + e.getMessage());
        }
        return "redirect:/resident/booking-history";
    }
}