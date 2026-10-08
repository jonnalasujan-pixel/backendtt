package com.pharmacy.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pharmacy.entity.Medicine;
import com.pharmacy.repository.MedicineRepository;

@Service
public class MedicineService {

    @Autowired
    private MedicineRepository medicineRepository;

    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    public Optional<Medicine> getMedicineById(Long id) {
        return medicineRepository.findById(id);
    }

    public List<Medicine> searchMedicines(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return medicineRepository.findAll();
        }
        return medicineRepository.searchMedicines(keyword.trim());
    }

    public List<Medicine> getLowStockMedicines() {
        return medicineRepository.findLowStockMedicines();
    }

    public List<Medicine> getExpiringSoonMedicines(int daysThreshold) {
        LocalDate thresholdDate = LocalDate.now().plusDays(daysThreshold);
        return medicineRepository.findExpiringSoonMedicines(thresholdDate);
    }

    @Transactional
    public Medicine saveMedicine(Medicine medicine) {
        return medicineRepository.save(medicine);
    }

    @Transactional
    public Medicine updateMedicine(Long id, Medicine updated) {
        Medicine existing = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found with id: " + id));

        existing.setName(updated.getName());
        existing.setGenericName(updated.getGenericName());
        existing.setType(updated.getType());
        existing.setBatchNumber(updated.getBatchNumber());
        existing.setStockQuantity(updated.getStockQuantity());
        existing.setMinStockThreshold(updated.getMinStockThreshold());
        existing.setUnitPrice(updated.getUnitPrice());
        existing.setMfgDate(updated.getMfgDate());
        existing.setExpDate(updated.getExpDate());
        if (updated.getCategory() != null) {
            existing.setCategory(updated.getCategory());
        }
        if (updated.getSupplier() != null) {
            existing.setSupplier(updated.getSupplier());
        }

        return medicineRepository.save(existing);
    }

    @Transactional
    public void deleteMedicine(Long id) {
        if (!medicineRepository.existsById(id)) {
            throw new RuntimeException("Medicine not found with id: " + id);
        }
        medicineRepository.deleteById(id);
    }
}
