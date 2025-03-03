package com.example.aptease.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.aptease.entity.DeliveryScore;
import com.example.aptease.entity.User;

@Repository
public interface DeliveryScoreRepository extends JpaRepository<DeliveryScore, Long> {
    Optional<DeliveryScore> findByDeliveryAgent(User deliveryAgent);
    
    @Query("SELECT COUNT(sb) FROM ServiceBooking sb WHERE sb.deliveryAgent.id = :agentId AND sb.rating IS NOT NULL")
    long countRatingsByAgent(@Param("agentId") Long agentId);

    @Query("SELECT COALESCE(AVG(sb.rating), 0) FROM ServiceBooking sb WHERE sb.deliveryAgent.id = :agentId AND sb.rating IS NOT NULL")
    double findAverageCustomerRating(@Param("agentId") Long agentId);
}
