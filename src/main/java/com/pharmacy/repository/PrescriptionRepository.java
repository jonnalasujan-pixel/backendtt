package com.pharmacy.repository;

import com.pharmacy.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    long countByCustomerId(Long customerId);
    List<Prescription> findByStatusOrderByCreatedAtDesc(String status);
    List<Prescription> findAllByOrderByCreatedAtDesc();
    long countByStatus(String status);
}
