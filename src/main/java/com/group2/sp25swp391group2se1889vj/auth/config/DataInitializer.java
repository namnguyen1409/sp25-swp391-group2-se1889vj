package com.group2.sp25swp391group2se1889vj.auth.config;


import com.group2.sp25swp391group2se1889vj.user.entity.User;
import com.group2.sp25swp391group2se1889vj.user.repository.UserRepository;
import com.group2.sp25swp391group2se1889vj.user.roles.RoleType;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer {
    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.firstname}")
    private String adminFirstname;

    @Value("${admin.lastname}")
    private String adminLastname;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.phone}")
    private String adminPhone;

    @Value("${admin.gender}")
    private Boolean adminGender;

    @Value("${admin.birthday}")
    private String adminBirthday;

    @Value("${admin.address}")
    private String adminAddress;

    private final UserRepository userRepository;

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            User admin = new User();
            admin.setUsername(adminUsername);
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String encodedPassword = encoder.encode(adminPassword);
            admin.setPass(encodedPassword);
            admin.setRole(RoleType.ADMIN);
            admin.setFirstName(adminFirstname);
            admin.setLastName(adminLastname);
            admin.setEmail(adminEmail);
            admin.setPhone(adminPhone);
            admin.setGender(adminGender);
            admin.setDob(LocalDate.parse(adminBirthday));
            admin.setAddress(adminAddress);
            userRepository.save(admin);
            System.out.println("Admin created");
        } else {
            System.out.println("Admin already exists");
        }
    }
}
