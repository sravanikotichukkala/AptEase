package com.example.aptease.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "service_bookings")
public class ServiceBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String serviceFlatNo;
    @Enumerated(EnumType.STRING)  // Store enum as a string
    @Column(name = "apt_service", nullable = false)
    private AptService aptService;

    @Column(name = "service_type", nullable = false)
    private String serviceType;  // Store the enum's display name

    @Column(name = "booking_time")
    private LocalDateTime bookingTime;

    @Column(name = "completion_time")
    private LocalDateTime completionTime;

    @ManyToOne
    @JoinColumn(name = "delivery_agent_id")
    private User deliveryAgent;  // Changed from DeliveryAgent to User

    @ManyToOne
    @JoinColumn(name = "resident_id")
    private User resident;   

    @Column(name = "notes")
    private String notes;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ServiceStatus status;
    
    @Column(name = "rating")
    private Integer rating;  // 1-5 stars

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AptService getAptService() {
		return aptService;
	}

	public void setAptService(AptService aptService) {
		this.aptService = aptService;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public LocalDateTime getBookingTime() {
		return bookingTime;
	}

	public void setBookingTime(LocalDateTime bookingTime) {
		this.bookingTime = bookingTime;
	}

	public LocalDateTime getCompletionTime() {
		return completionTime;
	}

	public void setCompletionTime(LocalDateTime completionTime) {
		this.completionTime = completionTime;
	}

    public User getDeliveryAgent() {
        return deliveryAgent;
    }

    public void setDeliveryAgent(User deliveryAgent) {
        this.deliveryAgent = deliveryAgent;
    }

    public User getResident() {
        return resident;
    }

    public void setResident(User resident) {
        this.resident = resident;
    }

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

    public ServiceStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceStatus status) {
        this.status = status;
    }

	@PrePersist
    public void setServiceTypeBeforeSave() {
        if (aptService != null) {
            this.serviceType = aptService.getDisplayName();
        }
    }
	 public String getServiceFlatNo() {
	        return serviceFlatNo;
	    }
	    public void setServiceFlatNo(String serviceFlatNo) {
	        this.serviceFlatNo = serviceFlatNo;
	    }

}
