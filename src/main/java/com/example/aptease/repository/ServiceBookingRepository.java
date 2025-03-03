package com.example.aptease.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.aptease.entity.ServiceBooking;
import com.example.aptease.entity.ServiceStatus;
import com.example.aptease.entity.User;

@Repository
public interface ServiceBookingRepository extends JpaRepository<ServiceBooking, Long> {
    List<ServiceBooking> findByResident(User resident);
    List<ServiceBooking> findByDeliveryAgent(User deliveryAgent);
    List<ServiceBooking> findByStatus(ServiceStatus status);
    List<ServiceBooking> findByResidentOrderByBookingTimeDesc(User resident);
    
    @Modifying
    @Transactional
    @Query("UPDATE ServiceBooking sb SET sb.status = :status WHERE sb.id = :bookingId")
    void updateServiceStatus(@Param("bookingId") Long bookingId, @Param("status") ServiceStatus status);
    
    @Query("SELECT sb FROM ServiceBooking sb WHERE sb.deliveryAgent = :deliveryAgent AND sb.status = 'PENDING'")
    List<ServiceBooking> findPendingBookingsForAgent(@Param("deliveryAgent") User deliveryAgent);
    
    @Query("SELECT sb FROM ServiceBooking sb WHERE sb.deliveryAgent = :deliveryAgent AND sb.status = 'COMPLETED'")
    List<ServiceBooking> findByDeliveryAgentAndStatus(@Param("deliveryAgent") User deliveryAgent, @Param("status") ServiceStatus status);
}