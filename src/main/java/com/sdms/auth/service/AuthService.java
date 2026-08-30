package com.sdms.auth.service;

import com.sdms.auth.dto.*;
import com.sdms.auth.entity.Role;
import com.sdms.auth.entity.User;
import com.sdms.auth.repository.UserRepository;
import com.sdms.auth.util.JwtUtil;
import com.sdms.beneficiary.entity.Beneficiary;
import com.sdms.beneficiary.repository.BeneficiaryRepository;
import com.sdms.common.exception.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalStateException("Username already taken");
        }
        if (beneficiaryRepository.findByAadhaarNumber(request.getAadhaarNumber()).isPresent()) {
            throw new IllegalStateException("A beneficiary with this Aadhaar number already exists");
        }

        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setFullName(request.getFullName());
        beneficiary.setAadhaarNumber(request.getAadhaarNumber());
        beneficiary.setDateOfBirth(request.getDateOfBirth());
        beneficiary.setGender(request.getGender());
        beneficiary.setPhoneNumber(request.getPhoneNumber());
        beneficiary.setEmail(request.getEmail());
        beneficiary.setAddress(request.getAddress());
        beneficiary.setRegion(request.getRegion());
        beneficiary.setAnnualIncome(request.getAnnualIncome());
        beneficiary.setBankAccountNumber(request.getBankAccountNumber());
        beneficiary.setIfscCode(request.getIfscCode());
        Beneficiary savedBeneficiary = beneficiaryRepository.save(beneficiary);

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(Role.BENEFICIARY);
        user.setBeneficiary(savedBeneficiary);
        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(
                savedUser.getUsername(),
                savedUser.getRole().name(),
                savedUser.getId(),
                savedBeneficiary.getId()
        );

        return new RegisterResponse(
                token,
                savedUser.getUsername(),
                savedUser.getFullName(),
                savedUser.getRole().name()
        );
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        if (!user.isActive()) {
            throw new InvalidCredentialsException("This account has been deactivated");
        }

        Long beneficiaryId = (user.getBeneficiary() != null) ? user.getBeneficiary().getId() : null;
        String token = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole().name(),
                user.getId(),
                beneficiaryId
        );

        return new LoginResponse(
                token,
                user.getUsername(),
                user.getFullName(),
                user.getRole().name()
        );
    }
}