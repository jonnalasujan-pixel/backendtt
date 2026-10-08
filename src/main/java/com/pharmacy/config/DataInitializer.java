package com.pharmacy.config;

import com.pharmacy.entity.Category;
import com.pharmacy.entity.Medicine;
import com.pharmacy.entity.Supplier;
import com.pharmacy.entity.User;
import com.pharmacy.repository.CategoryRepository;
import com.pharmacy.repository.MedicineRepository;
import com.pharmacy.repository.SupplierRepository;
import com.pharmacy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            // 1. Admin Login (admin / admin@123)
            userRepository.save(new User("admin", "admin@sanjeevani-pharma.in",
                    passwordEncoder.encode("admin@123"), "ADMIN",
                    "Rajesh Sharma (Store Admin)", "+91 98765 43210", "Pharmacy Central Office, Bengaluru"));

            // 2. Pharmacist Login (pharmacist / pharma@123)
            userRepository.save(new User("pharmacist", "pharmacist@sanjeevani-pharma.in",
                    passwordEncoder.encode("pharma@123"), "PHARMACIST",
                    "Suresh Patel (Reg. Pharmacist)", "+91 98450 12345", "Counter 1, Main Dispensary"));

            // 3. Customer Login (customer / customer@123)
            userRepository.save(new User("customer", "ananya.verma@gmail.com",
                    passwordEncoder.encode("customer@123"), "CUSTOMER",
                    "Ananya Verma", "+91 99123 45678", "#42, 3rd Cross, Indiranagar, Bengaluru"));
        }

        if (categoryRepository.count() == 0) {
            Category cTablets = categoryRepository.save(new Category("Tablets & Capsules", "Oral solid formulations"));
            Category cSyrups = categoryRepository.save(new Category("Syrups & Suspensions", "Oral liquid formulations"));
            Category cDigestive = categoryRepository.save(new Category("Digestive & Antacids", "Acidity and digestion relief"));
            Category cVitamins = categoryRepository.save(new Category("Vitamins & Supplements", "Immunity and supplements"));

            Supplier s1 = supplierRepository.save(new Supplier("Micro Labs Ltd.", "Ramesh Nair", "orders@microlabs.in", "+91 80 2234 5678", "Bengaluru, Karnataka"));
            Supplier s2 = supplierRepository.save(new Supplier("GlaxoSmithKline India", "Priya Sundaram", "distributors@gsk.in", "+91 22 2495 9000", "Mumbai, Maharashtra"));
            Supplier s3 = supplierRepository.save(new Supplier("Alkem Laboratories Ltd.", "Vikas Deshmukh", "supply@alkem.com", "+91 22 3982 9999", "Mumbai, Maharashtra"));
            Supplier s4 = supplierRepository.save(new Supplier("Glenmark Pharmaceuticals", "Kavita Rao", "sales@glenmarkpharma.com", "+91 22 4018 9999", "Mumbai, Maharashtra"));
            Supplier s5 = supplierRepository.save(new Supplier("Abbott Healthcare India", "Sunil Chopra", "care@abbott.in", "+91 22 3816 2000", "Baddi, Himachal Pradesh"));

            // Indian Tablets
            createMedicine("Dolo 650mg Tablet", "Paracetamol 650mg", "BAT-DOL-2025-01", 180, 30,
                    BigDecimal.valueOf(32.50), LocalDate.now().minusMonths(3), LocalDate.now().plusYears(2), cTablets, s1);

            createMedicine("Augmentin 625 Duo Tablet", "Amoxicillin + Clavulanic Acid", "BAT-AUG-2025-04", 65, 20,
                    BigDecimal.valueOf(204.00), LocalDate.now().minusMonths(2), LocalDate.now().plusYears(2), cTablets, s2);

            createMedicine("Pan-D Capsule", "Pantoprazole + Domperidone SR", "BAT-PND-2024-11", 8, 25,
                    BigDecimal.valueOf(195.00), LocalDate.now().minusMonths(6), LocalDate.now().plusMonths(2), cTablets, s3);

            createMedicine("Combiflam Tablet", "Ibuprofen + Paracetamol", "BAT-CMB-2024-08", 210, 40,
                    BigDecimal.valueOf(46.00), LocalDate.now().minusMonths(4), LocalDate.now().plusYears(2), cTablets, s3);

            createMedicine("Telma 40 Tablet", "Telmisartan 40mg (BP Care)", "BAT-TEL-2025-03", 95, 20,
                    BigDecimal.valueOf(118.00), LocalDate.now().minusMonths(2), LocalDate.now().plusYears(2), cTablets, s4);

            createMedicine("Limcee 500mg Chewable", "Ascorbic Acid (Vitamin C)", "BAT-LIM-2024-03", 5, 25,
                    BigDecimal.valueOf(28.50), LocalDate.now().minusYears(1), LocalDate.now().plusDays(22), cVitamins, s5);

            // Indian Syrups
            createMedicine("Benadryl Cough Syrup (100ml)", "Diphenhydramine + Ammonium Chloride", "BAT-BND-2025-01", 55, 15,
                    BigDecimal.valueOf(115.00), LocalDate.now().minusMonths(2), LocalDate.now().plusYears(2), cSyrups, s1);

            createMedicine("Ascoril-D Plus Syrup (100ml)", "Dextromethorphan + Phenylephrine", "BAT-ASC-2025-02", 40, 12,
                    BigDecimal.valueOf(128.00), LocalDate.now().minusMonths(1), LocalDate.now().plusYears(2), cSyrups, s4);

            createMedicine("Gelusil Antacid Syrup (200ml)", "Aluminium + Magnesium Hydroxide", "BAT-GEL-2024-09", 7, 15,
                    BigDecimal.valueOf(138.00), LocalDate.now().minusMonths(6), LocalDate.now().plusMonths(1), cDigestive, s2);

            createMedicine("Calpol Paediatric Syrup (60ml)", "Paracetamol Paediatric 120mg/5ml", "BAT-CLP-2024-05", 35, 15,
                    BigDecimal.valueOf(42.00), LocalDate.now().minusYears(1), LocalDate.now().plusDays(25), cSyrups, s2);

            createMedicine("Aristozyme Digestive Syrup (200ml)", "Diastase + Pepsin Enzymes", "BAT-ARZ-2025-03", 48, 10,
                    BigDecimal.valueOf(148.00), LocalDate.now().minusMonths(2), LocalDate.now().plusYears(2), cDigestive, s3);

            createMedicine("Becosules Performance Syrup (225ml)", "B-Complex + L-Lysine Minerals", "BAT-BCS-2025-04", 62, 15,
                    BigDecimal.valueOf(195.00), LocalDate.now().minusMonths(1), LocalDate.now().plusYears(2), cVitamins, s2);
        }
    }

    private void createMedicine(String name, String genericName, String batchNumber, int stock, int minStock,
                                BigDecimal price, LocalDate mfg, LocalDate exp, Category cat, Supplier sup) {
        Medicine m = new Medicine();
        m.setName(name);
        m.setGenericName(genericName);
        m.setBatchNumber(batchNumber);
        m.setStockQuantity(stock);
        m.setMinStockThreshold(minStock);
        m.setUnitPrice(price);
        m.setMfgDate(mfg);
        m.setExpDate(exp);
        m.setCategory(cat);
        m.setSupplier(sup);
        medicineRepository.save(m);
    }
}
