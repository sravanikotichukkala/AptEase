package com.example.aptease.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "service_booking_id")
    private ServiceBooking serviceBooking;

    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public ServiceBooking getServiceBooking() {
		return serviceBooking;
	}
	public void setServiceBooking(ServiceBooking serviceBooking) {
		this.serviceBooking = serviceBooking;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	private String itemName;
    private Integer quantity;
    private Double price;

    // Getters and setters
}
