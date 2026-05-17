package com.example.userservice.config;

import com.example.userservice.entity.AppUser;
import com.example.userservice.entity.Role;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.RoleService;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedDefaultSecurityData(RoleService roleService,
                                              UserRepository userRepository,
                                              PasswordEncoder passwordEncoder,
                                              @Value("${app.bootstrap.admin-username}") String adminUsername,
                                              @Value("${app.bootstrap.admin-password}") String adminPassword) {
        return args -> {
            Role userRole = roleService.ensureRole("USER", Set.of("PROFILE_READ"));
            Role adminRole = roleService.ensureRole("ADMIN", Set.of("ROLE_READ", "ROLE_WRITE", "USER_ROLE_WRITE"));

            if (!userRepository.existsByUsername(adminUsername)) {
                AppUser admin = new AppUser(adminUsername, null, passwordEncoder.encode(adminPassword));
                admin.setRoles(Set.of(userRole, adminRole));
                userRepository.save(admin);
            }
        };
    }
}
