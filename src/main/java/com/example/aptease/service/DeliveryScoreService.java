package com.example.aptease.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aptease.entity.DeliveryScore;
import com.example.aptease.entity.User;
import com.example.aptease.repository.DeliveryScoreRepository;
import com.example.aptease.repository.UserRepository;

@Service
public class DeliveryScoreService {
    private final DeliveryScoreRepository scoreRepository;
    private final UserRepository userRepository;

    @Autowired
    public DeliveryScoreService(DeliveryScoreRepository scoreRepository, UserRepository userRepository) {
        this.scoreRepository = scoreRepository;
        this.userRepository = userRepository;
    }

    public DeliveryScore getScore(User deliveryAgent) {
        return scoreRepository.findByDeliveryAgent(deliveryAgent)
                .orElseGet(() -> createOrFetchExistingScore(deliveryAgent));
    }

    private DeliveryScore createOrFetchExistingScore(User deliveryAgent) {
        return scoreRepository.findByDeliveryAgent(deliveryAgent)
                .orElseGet(() -> {
                    DeliveryScore score = new DeliveryScore();
                    score.setDeliveryAgent(deliveryAgent);
                    score.setCustomerRatingScore(0.0);
                    score.setOverallScore(0.0);
                    return scoreRepository.save(score);
                });
    }

    public DeliveryScore updateScore(DeliveryScore score) {
        // Calculate overall score based on customer rating score
        double overall = score.getCustomerRatingScore();
        score.setOverallScore(overall);
        return scoreRepository.save(score);
    }

    // ✅ Update delivery agent's score when a new rating is added
    public void updateDeliveryAgentScore(Long deliveryAgentId) {
        // Fetch the delivery agent from UserRepository
        User deliveryAgent = userRepository.findById(deliveryAgentId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery agent not found"));

        DeliveryScore score = getScore(deliveryAgent);

        // Calculate new customer rating score as an average of past ratings
        double updatedCustomerRating = scoreRepository.findAverageCustomerRating(deliveryAgentId);
        
        score.setCustomerRatingScore(updatedCustomerRating);
        updateScore(score);
    }
}
