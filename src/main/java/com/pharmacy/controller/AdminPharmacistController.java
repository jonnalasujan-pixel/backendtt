package com.pharmacy.controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pharmacy.dto.PharmacistSummaryDto;
import com.pharmacy.dto.ManagedAccountUpdateRequest;
import com.pharmacy.dto.RegisterRequest;
import com.pharmacy.entity.User;
import com.pharmacy.repository.UserRepository;
import com.pharmacy.service.AuthService;

@RestController
@RequestMapping("/api/admin/pharmacists")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPharmacistController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<PharmacistSummaryDto> getPharmacists() {
        return userRepository.findAllByRole("PHARMACIST").stream()
                .map(PharmacistSummaryDto::new)
            .collect(java.util.stream.Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<?> createPharmacist(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = authService.createPharmacist(request);
            return ResponseEntity.ok(new PharmacistSummaryDto(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePharmacist(@PathVariable Long id, @RequestBody ManagedAccountUpdateRequest request) {
        try {
            return ResponseEntity.ok(new PharmacistSummaryDto(
                    authService.updateManagedAccount(id, "PHARMACIST", request)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePharmacist(@PathVariable Long id) {
        try {
            authService.deleteManagedAccount(id, "PHARMACIST");
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", e.getMessage()));
        }
    }
}