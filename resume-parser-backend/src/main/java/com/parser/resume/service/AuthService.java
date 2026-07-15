package com.parser.resume.service;

import com.parser.resume.dto.AuthRequestDTO;
import com.parser.resume.dto.AuthResponseDTO;
import com.parser.resume.model.AccessCode;
import com.parser.resume.model.AccessCodeLog;
import com.parser.resume.model.Role;
import com.parser.resume.model.User;
import com.parser.resume.repository.AccessCodeLogRepository;
import com.parser.resume.repository.AccessCodeRepository;
import com.parser.resume.repository.RoleRepository;
import com.parser.resume.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AccessCodeRepository accessCodeRepository;

    @Autowired
    private AccessCodeLogRepository accessCodeLogRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public void initializeDefaults() {
        Role adminRole = roleRepository.findByName("ADMIN").orElseGet(() -> roleRepository.save(new Role("ADMIN")));
        Role viewerRole = roleRepository.findByName("VIEWER").orElseGet(() -> roleRepository.save(new Role("VIEWER")));

        if (!userRepository.existsByEmail("admin")) {
            User admin = new User();
            admin.setFullName("Administrator");
            admin.setEmail("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(adminRole);
            admin.setEnabled(true);
            userRepository.save(admin);
        }

        if (!accessCodeRepository.findByCode("ADMIN-DEFAULT").isPresent()) {
            AccessCode defaultCode = new AccessCode();
            defaultCode.setCode("ADMIN-DEFAULT");
            defaultCode.setAssignedName("Default Viewer");
            defaultCode.setAssignedEmail("viewer@example.com");
            defaultCode.setExpiryDate(LocalDateTime.now().plusYears(1));
            defaultCode.setMaxUses(10);
            defaultCode.setUsedCount(0);
            defaultCode.setActive(true);
            defaultCode.setDisabled(false);
            accessCodeRepository.save(defaultCode);
        }
    }

    @Transactional
    public AuthResponseDTO login(AuthRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        String token = UUID.randomUUID().toString();
        return new AuthResponseDTO(token, user.getRole().getName(), user.getEmail(), user.getFullName());
    }

    @Transactional
    public AuthResponseDTO registerViewer(AuthRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        AccessCode accessCode = accessCodeRepository.findByCode(request.getAccessCode()).orElse(null);
        if (accessCode == null || !accessCode.isActive() || accessCode.isDisabled()) {
            throw new IllegalArgumentException("Invalid access code");
        }
        if (accessCode.getExpiryDate() != null && accessCode.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Access code expired");
        }
        if (accessCode.getMaxUses() != null && accessCode.getUsedCount() >= accessCode.getMaxUses()) {
            throw new IllegalArgumentException("Access code has reached max uses");
        }
        if (accessCode.getAssignedEmail() != null && !accessCode.getAssignedEmail().equalsIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Access code is assigned to a different email");
        }

        Role viewerRole = roleRepository.findByName("VIEWER").orElseGet(() -> roleRepository.save(new Role("VIEWER")));
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(viewerRole);
        user.setEnabled(true);
        userRepository.save(user);

        accessCode.setUsedCount(accessCode.getUsedCount() + 1);
        accessCodeRepository.save(accessCode);

        AccessCodeLog log = new AccessCodeLog();
        log.setAccessCode(accessCode);
        log.setEmail(request.getEmail());
        log.setStatus("USED");
        log.setUsedAt(LocalDateTime.now());
        accessCodeLogRepository.save(log);

        String token = UUID.randomUUID().toString();
        return new AuthResponseDTO(token, viewerRole.getName(), user.getEmail(), user.getFullName());
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
