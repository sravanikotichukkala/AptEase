package com.example.aptease.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.aptease.entity.Role;
import com.example.aptease.entity.User;
import com.example.aptease.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())  // Password is already encrypted in database
                .roles(user.getRole().name())
                .build();
    }

    public User saveUser(User user) {
        // Encrypt password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // Method to update user without changing password
    public User updateUser(User user) {
        if (user.getId() != null) {
            User existingUser = userRepository.findById(user.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // If password is empty or null, keep the existing password
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            } else {
                // If new password is provided, encrypt it
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
        }
        return userRepository.save(user);
    }

    public List<User> getAllResidents() {
        return userRepository.findByRole(Role.RESIDENT);
    }

    public List<User> getAllDeliveryAgents() {
        return userRepository.findByRole(Role.DELIVERY_AGENT);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    
    public List<User> findDeliveryAgentsByFlatNo(Integer flatNo) {
        return userRepository.findByRoleAndFlatNo(Role.DELIVERY_AGENT, flatNo);
    }

    public User getDeliveryAgentForResident(User resident) {
        return userRepository.findFirstByRoleAndFlatNo(Role.DELIVERY_AGENT, resident.getFlatNo())
            .orElse(null);
    }
}