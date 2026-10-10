package com.pharmacy.service;

import java.util.Collections;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.pharmacy.config.JwtUtils;
import com.pharmacy.dto.AuthRequest;
import com.pharmacy.dto.AuthResponse;
import com.pharmacy.dto.ManagedAccountUpdateRequest;
import com.pharmacy.dto.RegisterRequest;
import com.pharmacy.entity.User;
import com.pharmacy.repository.UserRepository;
import com.pharmacy.repository.OrderRepository;
import com.pharmacy.repository.PrescriptionRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Value("${google.client-id:}")
    private String googleClientId;

    public AuthResponse login(AuthRequest request) {
        String login = request.getUsername().trim();
        Optional<User> userOpt = login.contains("@")
            ? userRepository.findByEmail(login.toLowerCase(Locale.ROOT))
            : userRepository.findByUsername(login);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("Invalid username or password");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtUtils.generateToken(user.getUsername(), user.getRole());
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getEmail(), user.getRole(), user.getFullName());
    }

    public AuthResponse register(RegisterRequest request) {
        String username = request.getUsername().trim();
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName().trim());
        user.setPhone(request.getPhone() == null ? null : request.getPhone().trim());
        user.setAddress(request.getAddress());
        user.setRole("CUSTOMER");

        User savedUser = userRepository.save(user);
        String token = jwtUtils.generateToken(savedUser.getUsername(), savedUser.getRole());
        return new AuthResponse(token, savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(), savedUser.getRole(), savedUser.getFullName());
    }

    public AuthResponse loginWithGoogle(String credential) throws Exception {
        if (googleClientId == null || googleClientId.trim().isEmpty()) {
            throw new IllegalStateException("Google sign-in is not configured on the server");
        }

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleClientId))
                .build();
        GoogleIdToken idToken = verifier.verify(credential);
        if (idToken == null || !Boolean.TRUE.equals(idToken.getPayload().getEmailVerified())) {
            throw new RuntimeException("Google account could not be verified");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail().toLowerCase(Locale.ROOT);
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null && !"CUSTOMER".equals(user.getRole())) {
            throw new RuntimeException("Google sign-in is available for customer accounts only");
        }

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setUsername(createGoogleUsername(email));
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            user.setRole("CUSTOMER");
            Object name = payload.get("name");
            user.setFullName(name == null ? email.substring(0, email.indexOf('@')) : name.toString());
            user = userRepository.save(user);
        }

        String token = jwtUtils.generateToken(user.getUsername(), user.getRole());
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getEmail(), user.getRole(), user.getFullName());
    }

    public User createPharmacist(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        User user = new User(request.getUsername(), request.getEmail(),
                passwordEncoder.encode(request.getPassword()), "PHARMACIST",
                request.getFullName(), request.getPhone(), request.getAddress());
        return userRepository.save(user);
    }

    public User createCustomer(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        User user = new User(request.getUsername(), request.getEmail(),
                passwordEncoder.encode(request.getPassword()), "CUSTOMER",
                request.getFullName(), request.getPhone(), request.getAddress());
        return userRepository.save(user);
    }

    public User updateManagedAccount(Long id, String role, ManagedAccountUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        if (!role.equals(user.getRole())) {
            throw new RuntimeException("Account not found");
        }

        if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
            user.setFullName(request.getFullName().trim());
        }
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
            Optional<User> existing = userRepository.findByEmail(email);
            if (existing.isPresent() && !existing.get().getId().equals(id)) {
                throw new RuntimeException("Email is already in use");
            }
            user.setEmail(email);
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            if (request.getPassword().length() < 8) {
                throw new RuntimeException("Password must be at least 8 characters");
            }
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        return userRepository.save(user);
    }

    public void deleteManagedAccount(Long id, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        if (!role.equals(user.getRole())) {
            throw new RuntimeException("Account not found");
        }
        if ("CUSTOMER".equals(role)
                && (orderRepository.countByCustomerId(id) > 0 || prescriptionRepository.countByCustomerId(id) > 0)) {
            throw new RuntimeException("Customer has order or prescription history and cannot be deleted");
        }
        userRepository.delete(user);
    }

    private String createGoogleUsername(String email) {
        String localPart = email.substring(0, email.indexOf('@'))
                .replaceAll("[^A-Za-z0-9._-]", "_");
        String base = localPart.length() > 42 ? localPart.substring(0, 42) : localPart;
        if (base.length() < 3) {
            base = "customer";
        }

        String username = base;
        int suffix = 2;
        while (userRepository.existsByUsername(username)) {
            String ending = String.valueOf(suffix++);
            username = base.substring(0, Math.min(base.length(), 50 - ending.length())) + ending;
        }
        return username;
    }
}
