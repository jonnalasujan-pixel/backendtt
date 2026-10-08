package com.pharmacy.service;

import com.pharmacy.entity.Prescription;
import com.pharmacy.entity.User;
import com.pharmacy.repository.PrescriptionRepository;
import com.pharmacy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Prescription> getAllPrescriptions() {
        return prescriptionRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Prescription> getPrescriptionsByCustomerId(Long customerId) {
        return prescriptionRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    public List<Prescription> getPrescriptionsByStatus(String status) {
        return prescriptionRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    public Optional<Prescription> getPrescriptionById(Long id) {
        return prescriptionRepository.findById(id);
    }

    @Transactional
    public Prescription submitPrescription(Long customerId, String doctorName, String patientName, String notes, String filePath) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));

        Prescription prescription = new Prescription();
        prescription.setCustomer(customer);
        prescription.setDoctorName(doctorName);
        prescription.setPatientName(patientName);
        prescription.setNotes(notes);
        prescription.setFilePath(filePath);
        prescription.setStatus("PENDING");
        prescription.setCreatedAt(LocalDateTime.now());

        return prescriptionRepository.save(prescription);
    }

    @Transactional
    public Prescription updateStatus(Long id, String status) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescription not found with id: " + id));

        prescription.setStatus(status.toUpperCase());
        return prescriptionRepository.save(prescription);
    }
}
