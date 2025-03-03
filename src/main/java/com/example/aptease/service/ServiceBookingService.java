package com.example.aptease.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aptease.entity.AptService;
import com.example.aptease.entity.OrderItem;
import com.example.aptease.entity.ServiceBooking;
import com.example.aptease.entity.ServiceStatus;
import com.example.aptease.entity.User;
import com.example.aptease.repository.ServiceBookingRepository;
import com.example.aptease.repository.UserRepository;

@Service
public class ServiceBookingService {
    
    private final ServiceBookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final JavaMailSender emailSender;
    private final DeliveryScoreService scoreService;

    @Autowired
    public ServiceBookingService(ServiceBookingRepository bookingRepository, 
                                 UserRepository userRepository,
                                 UserService userService,
                                 JavaMailSender emailSender,
                                 DeliveryScoreService scoreService) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.emailSender = emailSender;
        this.scoreService = scoreService;
    }

    @Transactional
    public ServiceBooking createBooking(User resident, AptService aptService, 
                                        List<OrderItem> items, String notes) {
        ServiceBooking booking = new ServiceBooking();
        booking.setResident(resident);
        booking.setAptService(aptService);
        booking.setStatus(ServiceStatus.PENDING);
        booking.setBookingTime(LocalDateTime.now());
        booking.setNotes(notes);

        User assignedAgent = findAvailableAgent(resident.getFlatNo());
        if (assignedAgent != null) {
            booking.setDeliveryAgent(assignedAgent);
        }

        booking = bookingRepository.save(booking);

        if (items != null) {
            for (OrderItem item : items) {
                item.setServiceBooking(booking);
            }   
        }

        if (booking.getDeliveryAgent() != null) {
            notifyDeliveryAgentsForFlat(booking);
        }
        
        return booking;
    }

    private User findAvailableAgent(Integer flatNo) {
        List<User> availableAgents = userRepository.findAvailableAgentsByFlatNo(flatNo);

        if (!availableAgents.isEmpty()) {
            return availableAgents.get(0);
        }
        
        return null;
    }

    private void notifyDeliveryAgentsForFlat(ServiceBooking booking) {
        Integer residentFlatNo = booking.getResident().getFlatNo();
        List<User> deliveryAgents = userService.findDeliveryAgentsByFlatNo(residentFlatNo);

        for (User agent : deliveryAgents) {
            String loginUrl = "http://localhost:8082/login";
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(agent.getEmail());
            message.setSubject("New Service Request - " + booking.getAptService().getDisplayName());
            message.setText(String.format(
                "Dear %s,\n\n" +
                "A new service request has been placed for your flat:\n" +
                "Service: %s\n" +
                "Flat No: %d\n" +
                "Notes: %s\n\n" +
                "Please log in to the system to accept this request by clicking the link below:\n" +
                "%s\n\n" +
                "Best regards,\nAptEase Team",
                agent.getName(),
                booking.getAptService().getDisplayName(),
                residentFlatNo,
                booking.getNotes(),
                loginUrl
            ));
            emailSender.send(message);
        }
    }

    public List<ServiceBooking> getResidentBookingHistory(User resident) {
        return bookingRepository.findByResidentOrderByBookingTimeDesc(resident);
    }

    @Transactional
    public void addRatingAndComplete(Long orderId, int rating) {
        ServiceBooking booking = bookingRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        
        if (booking.getStatus() != ServiceStatus.IN_PROGRESS) {
            throw new IllegalStateException("Order is not in progress");
        }

        booking.setStatus(ServiceStatus.COMPLETED);
        booking.setRating(rating);
        bookingRepository.save(booking);

        if (booking.getDeliveryAgent() != null) {
            scoreService.updateDeliveryAgentScore(booking.getDeliveryAgent().getId());
        }
    }

    public List<ServiceBooking> getRecentBookings(User resident, int limit) {
        return bookingRepository.findByResidentOrderByBookingTimeDesc(resident)
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<ServiceBooking> getPendingBookingsForAgent(User deliveryAgent) {
        return bookingRepository.findPendingBookingsForAgent(deliveryAgent);
    }
    
    @Transactional
    public void updateToInProgress(Long bookingId) {
        ServiceBooking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(ServiceStatus.IN_PROGRESS);
        bookingRepository.save(booking);
    }
}
