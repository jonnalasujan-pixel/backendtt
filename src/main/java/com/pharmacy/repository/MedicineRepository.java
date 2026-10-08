package com.pharmacy.repository;

import com.pharmacy.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    @Query("SELECT m FROM Medicine m WHERE m.stockQuantity <= m.minStockThreshold")
    List<Medicine> findLowStockMedicines();

    @Query("SELECT COUNT(m) FROM Medicine m WHERE m.stockQuantity <= m.minStockThreshold")
    long countLowStockMedicines();

    @Query("SELECT m FROM Medicine m WHERE m.expDate <= :expiryThreshold")
    List<Medicine> findExpiringSoonMedicines(@Param("expiryThreshold") LocalDate expiryThreshold);

    @Query("SELECT COUNT(m) FROM Medicine m WHERE m.expDate <= :expiryThreshold")
    long countExpiringSoonMedicines(@Param("expiryThreshold") LocalDate expiryThreshold);

    @Query("SELECT m FROM Medicine m WHERE " +
           "LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.genericName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.batchNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Medicine> searchMedicines(@Param("keyword") String keyword);

    List<Medicine> findByCategoryId(Long categoryId);
}
