package com.example.aptease.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "delivery_scores")
public class DeliveryScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "delivery_agent_id")
    private User deliveryAgent;

    private double onTimeDeliveryScore;
    private double customerRatingScore;
    private double overallScore;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getDeliveryAgent() {
        return deliveryAgent;
    }

    public void setDeliveryAgent(User deliveryAgent) {
        this.deliveryAgent = deliveryAgent;
    }

    public double getOnTimeDeliveryScore() {
        return onTimeDeliveryScore;
    }

    public void setOnTimeDeliveryScore(double onTimeDeliveryScore) {
        this.onTimeDeliveryScore = onTimeDeliveryScore;
    }

    public double getCustomerRatingScore() {
        return customerRatingScore;
    }

    public void setCustomerRatingScore(double customerRatingScore) {
        this.customerRatingScore = customerRatingScore;
    }

    public double getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(double overallScore) {
        this.overallScore = overallScore;
    }
}