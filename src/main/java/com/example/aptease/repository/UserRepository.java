package com.example.aptease.repository; // Ensure this is the correct package

import com.example.aptease.entity.User;
import com.example.aptease.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findByRole(Role role);
    List<User> findByRoleAndFlatNo(Role role, Integer flatNo);
    Optional<User> findFirstByRoleAndFlatNo(Role role, Integer flatNo);
    
    @Query("SELECT u FROM User u WHERE u.role = 'DELIVERY_AGENT' AND u.flatNo = :flatNo AND u.isAvailable = true")
    List<User> findAvailableAgentsByFlatNo(@Param("flatNo") Integer flatNo);
    
    @Query("SELECT u FROM User u WHERE u.role = 'DELIVERY_AGENT' AND u.id = :agentId")
    Optional<User> findDeliveryAgentById(@Param("agentId") Long agentId);
}