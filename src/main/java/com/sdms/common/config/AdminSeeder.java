package com.sdms.common.config;

import com.sdms.auth.entity.Role;
import com.sdms.auth.entity.User;
import com.sdms.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.default-username}")
    private String defaultUsername;

    @Value("${admin.default-password}")
    private String defaultPassword;

    @Override
    public void run(String... args) {
        boolean adminExists = userRepository.findAll().stream()
                .anyMatch(user -> user.getRole() == Role.ADMIN);

        if (!adminExists) {
            User admin = new User();
            admin.setUsername(defaultUsername);
            admin.setPassword(passwordEncoder.encode(defaultPassword));
            admin.setFullName("System Administrator");
            admin.setRole(Role.ADMIN);
            admin.setActive(true);
            userRepository.save(admin);
            System.out.println("Default ADMIN account created: " + defaultUsername);
        }
    }
}