package com.pharmacy.controller;

import com.pharmacy.entity.Supplier;
import com.pharmacy.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/suppliers")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SupplierController {

    @Autowired
    private SupplierRepository supplierRepository;

    @GetMapping
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        return ResponseEntity.ok(supplierRepository.findAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createSupplier(@RequestBody Supplier supplier) {
        if (supplierRepository.existsByName(supplier.getName())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Supplier already exists with name: " + supplier.getName());
            return ResponseEntity.badRequest().body(error);
        }
        return ResponseEntity.ok(supplierRepository.save(supplier));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateSupplier(@PathVariable Long id, @RequestBody Supplier updated) {
        return supplierRepository.findById(id).map(supplier -> {
            if (!supplier.getName().equals(updated.getName()) && supplierRepository.existsByName(updated.getName())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Supplier already exists with name: " + updated.getName());
                return ResponseEntity.badRequest().body(error);
            }
            supplier.setName(updated.getName());
            supplier.setContactPerson(updated.getContactPerson());
            supplier.setEmail(updated.getEmail());
            supplier.setPhone(updated.getPhone());
            supplier.setAddress(updated.getAddress());
            return ResponseEntity.ok(supplierRepository.save(supplier));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteSupplier(@PathVariable Long id) {
        if (!supplierRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        supplierRepository.deleteById(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Supplier deleted successfully");
        return ResponseEntity.ok(response);
    }
}
