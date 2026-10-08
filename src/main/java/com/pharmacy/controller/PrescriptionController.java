package com.pharmacy.controller;

import com.pharmacy.entity.Prescription;
import com.pharmacy.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prescriptions")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @GetMapping
    public ResponseEntity<List<Prescription>> getAllPrescriptions(@RequestParam(required = false) String status) {
        if (status != null && !status.trim().isEmpty()) {
            return ResponseEntity.ok(prescriptionService.getPrescriptionsByStatus(status));
        }
        return ResponseEntity.ok(prescriptionService.getAllPrescriptions());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Prescription>> getCustomerPrescriptions(@PathVariable Long customerId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByCustomerId(customerId));
    }

    @PostMapping
    public ResponseEntity<?> submitPrescription(@RequestBody Map<String, Object> payload) {
        try {
            Long customerId = Long.valueOf(payload.get("customerId").toString());
            String doctorName = (String) payload.get("doctorName");
            String patientName = (String) payload.get("patientName");
            String notes = (String) payload.get("notes");
            String filePath = (String) payload.get("filePath");

            Prescription prescription = prescriptionService.submitPrescription(customerId, doctorName, patientName, notes, filePath);
            return ResponseEntity.ok(prescription);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String status = payload.get("status");
            if (status == null || status.trim().isEmpty()) {
                throw new RuntimeException("Status parameter is required");
            }
            Prescription updated = prescriptionService.updateStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
