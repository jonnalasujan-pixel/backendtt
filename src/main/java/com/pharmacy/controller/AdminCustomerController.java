package com.pharmacy.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

import com.pharmacy.dto.CustomerSummaryDto;
import com.pharmacy.dto.ManagedAccountUpdateRequest;
import com.pharmacy.dto.RegisterRequest;
import com.pharmacy.repository.UserRepository;
import com.pharmacy.service.AuthService;

@RestController
@RequestMapping("/api/admin/customers")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCustomerController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @GetMapping
    public List<CustomerSummaryDto> getCustomers() {
        return userRepository.findAllByRole("CUSTOMER").stream()
                .map(CustomerSummaryDto::new)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<?> createCustomer(@Valid @RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(new CustomerSummaryDto(authService.createCustomer(request)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @RequestBody ManagedAccountUpdateRequest request) {
        try {
            return ResponseEntity.ok(new CustomerSummaryDto(authService.updateManagedAccount(id, "CUSTOMER", request)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        try {
            authService.deleteManagedAccount(id, "CUSTOMER");
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", e.getMessage()));
        }
    }
}