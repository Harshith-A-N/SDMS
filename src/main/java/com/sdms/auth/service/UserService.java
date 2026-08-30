package com.sdms.auth.service;

import com.sdms.audit.entity.AuditAction;
import com.sdms.audit.service.AuditService;
import com.sdms.auth.dto.UserCreateRequest;
import com.sdms.auth.dto.UserResponse;
import com.sdms.auth.entity.User;
import com.sdms.auth.repository.UserRepository;
import com.sdms.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalStateException("Username already taken");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(request.getRole());
        user.setActive(true);

        User saved = userRepository.save(user);

        auditService.log(AuditAction.USER_CREATED, "User", saved.getId());

        return mapToResponse(saved);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void deactivateUser(Long id) {
        User user = getRequiredUser(id);
        user.setActive(false);
        userRepository.save(user);

        auditService.log(AuditAction.USER_DEACTIVATED, "User", user.getId());
    }

    public void activateUser(Long id) {
        User user = getRequiredUser(id);
        user.setActive(true);
        userRepository.save(user);

        auditService.log(AuditAction.USER_ACTIVATED, "User", user.getId());
    }

    private User getRequiredUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getRole().name(),
                user.isActive()
        );
    }
}