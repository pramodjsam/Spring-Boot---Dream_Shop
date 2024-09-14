package com.pramod.dreamshops.data;

import com.pramod.dreamshops.model.Role;
import com.pramod.dreamshops.model.User;
import com.pramod.dreamshops.repository.RoleRepository;
import com.pramod.dreamshops.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Transactional
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Set<String> defaultRoles = Set.of("ROLE_ADMIN", "ROLE_USER");
        this.createDefaultRolesIfNotExists(defaultRoles);
        this.createDefaultUserIfNotExists();
        this.createDefaultAdminIfNotExists();
    }

    private void createDefaultUserIfNotExists() {
        Role userRole = this.roleRepository.findByName("ROLE_USER").get();
        for (int i = 1; i <= 5; i++) {
            String defaultEmail = "user" + i + "@email.com";
            if (this.userRepository.existsByEmail(defaultEmail)) {
                continue;
            }
            User user = new User();
            user.setFirstName("The User");
            user.setLastName("User" + i);
            user.setEmail(defaultEmail);
            user.setPassword(this.passwordEncoder.encode("123456"));
            user.setRoles(Set.of(userRole));
            this.userRepository.save(user);
            System.out.println("Default user " + i + " created successfully");
        }
    }

    private void createDefaultRolesIfNotExists(Set<String> roles) {
        roles.stream()
                .filter(role -> this.roleRepository.findByName(role).isEmpty())
                .map((role) -> new Role(role))
                .forEach(role -> this.roleRepository.save(role));
    }

    private void createDefaultAdminIfNotExists() {
        Role adminRole = this.roleRepository.findByName("ROLE_ADMIN").get();
        for (int i = 1; i <= 2; i++) {
            String defaultEmail = "admin" + i + "@email.com";
            if (this.userRepository.existsByEmail(defaultEmail)) {
                continue;
            }
            User user = new User();
            user.setFirstName("Admin");
            user.setLastName("Admin" + i);
            user.setEmail(defaultEmail);
            user.setRoles(Set.of(adminRole));
            user.setPassword(this.passwordEncoder.encode("123456"));
            this.userRepository.save(user);
            System.out.println("Default admin user " + i + " created successfully");
        }
    }
}
