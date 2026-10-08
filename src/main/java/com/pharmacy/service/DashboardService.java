package com.pharmacy.service;

import com.pharmacy.dto.DashboardStatsDto;
import com.pharmacy.repository.MedicineRepository;
import com.pharmacy.repository.OrderRepository;
import com.pharmacy.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class DashboardService {

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    public DashboardStatsDto getDashboardStats() {
        long totalMedicines = medicineRepository.count();
        long lowStockCount = medicineRepository.countLowStockMedicines();
        long expiringCount = medicineRepository.countExpiringSoonMedicines(LocalDate.now().plusDays(30));
        long totalOrders = orderRepository.countCompletedOrders();
        BigDecimal totalRevenue = orderRepository.calculateTotalRevenue();
        long pendingPrescriptions = prescriptionRepository.countByStatus("PENDING");

        return new DashboardStatsDto(
                totalMedicines,
                lowStockCount,
                expiringCount,
                totalOrders,
                totalRevenue != null ? totalRevenue : BigDecimal.ZERO,
                pendingPrescriptions
        );
    }
}
