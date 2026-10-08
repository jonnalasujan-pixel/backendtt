-- Sanjeevani Medicos & Healthcare - MySQL Schema & Indian Seed Data
CREATE DATABASE IF NOT EXISTS pharmacy_db;
USE pharmacy_db;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'PHARMACIST', 'CUSTOMER') NOT NULL DEFAULT 'CUSTOMER',
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Categories Table
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- 3. Suppliers Table
CREATE TABLE IF NOT EXISTS suppliers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Medicines Table (Tablets, Syrups, Capsules)
CREATE TABLE IF NOT EXISTS medicines (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    generic_name VARCHAR(150),
    type VARCHAR(30) DEFAULT 'Tablet',
    batch_number VARCHAR(50) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    min_stock_threshold INT NOT NULL DEFAULT 10,
    unit_price DECIMAL(10, 2) NOT NULL,
    mfg_date DATE NOT NULL,
    exp_date DATE NOT NULL,
    category_id BIGINT,
    supplier_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE SET NULL
);

-- 5. Prescriptions Table
CREATE TABLE IF NOT EXISTS prescriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    doctor_name VARCHAR(100),
    patient_name VARCHAR(100),
    notes TEXT,
    file_path VARCHAR(255),
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 6. Orders Table (Indian GST Format)
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id BIGINT,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2) NOT NULL,
    tax_amount DECIMAL(10, 2) DEFAULT 0.00,
    discount_amount DECIMAL(10, 2) DEFAULT 0.00,
    net_amount DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(50) DEFAULT 'UPI (PhonePe/GPay)',
    status ENUM('COMPLETED', 'PENDING', 'CANCELLED') DEFAULT 'COMPLETED',
    FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 7. Order Items Table
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    medicine_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (medicine_id) REFERENCES medicines(id) ON DELETE RESTRICT
);

-- =========================================================================
-- 3 Authorized Users (admin / admin@123, pharmacist / pharma@123, customer / customer@123)
-- =========================================================================

INSERT IGNORE INTO users (id, username, email, password, role, full_name, phone, address) VALUES
(1, 'admin', 'admin@sanjeevani-pharma.in', '$2a$10$wK1WdC3mD/4BphkG3Z803O8O4jFzYf1sI8W3lY6gPqA3e3O9Z5K3a', 'ADMIN', 'Rajesh Sharma (Admin)', '+91 98765 43210', 'Pharmacy Central Office, Bengaluru'),
(2, 'pharmacist', 'pharmacist@sanjeevani-pharma.in', '$2a$10$wK1WdC3mD/4BphkG3Z803O8O4jFzYf1sI8W3lY6gPqA3e3O9Z5K3a', 'PHARMACIST', 'Suresh Patel (Reg. Pharmacist)', '+91 98450 12345', 'Counter 1, Main Dispensary'),
(3, 'customer', 'ananya.verma@gmail.com', '$2a$10$wK1WdC3mD/4BphkG3Z803O8O4jFzYf1sI8W3lY6gPqA3e3O9Z5K3a', 'CUSTOMER', 'Ananya Verma', '+91 99123 45678', '#42, 3rd Cross, Indiranagar, Bengaluru');

INSERT IGNORE INTO categories (id, name, description) VALUES
(1, 'Tablets & Capsules', 'Oral solid formulations, painkillers, antibiotics, BP/Diabetes tablets'),
(2, 'Syrups & Suspensions', 'Liquid oral formulations, cough syrups, antacids, pediatric suspensions'),
(3, 'Antibiotics & Anti-infectives', 'Broad-spectrum antibacterial medications'),
(4, 'Digestive & Antacids', 'Acidity relief and digestive enzymes'),
(5, 'Vitamins, Minerals & Calcium', 'Nutritional health supplements');

INSERT IGNORE INTO suppliers (id, name, contact_person, email, phone, address) VALUES
(1, 'Micro Labs Ltd.', 'Ramesh Nair', 'orders@microlabs.in', '+91 80 2234 5678', 'Bengaluru, Karnataka'),
(2, 'GlaxoSmithKline India', 'Priya Sundaram', 'distributors@gsk.in', '+91 22 2495 9000', 'Mumbai, Maharashtra'),
(3, 'Alkem Laboratories Ltd.', 'Vikas Deshmukh', 'supply@alkem.com', '+91 22 3982 9999', 'Mumbai, Maharashtra'),
(4, 'Glenmark Pharmaceuticals', 'Kavita Rao', 'sales@glenmarkpharma.com', '+91 22 4018 9999', 'Mumbai, Maharashtra'),
(5, 'Abbott Healthcare India', 'Sunil Chopra', 'care@abbott.in', '+91 22 3816 2000', 'Baddi, Himachal Pradesh');

-- Popular Indian Tablets and Syrups
INSERT IGNORE INTO medicines (id, name, generic_name, batch_number, stock_quantity, min_stock_threshold, unit_price, mfg_date, exp_date, category_id, supplier_id) VALUES
(1, 'Dolo 650mg Tablet', 'Paracetamol 650mg', 'BAT-DOL-2025-01', 180, 30, 32.50, '2025-01-15', '2027-12-31', 1, 1),
(2, 'Augmentin 625 Duo Tablet', 'Amoxicillin (500mg) + Clavulanic Acid (125mg)', 'BAT-AUG-2025-04', 65, 20, 204.00, '2025-02-10', '2027-01-20', 1, 2),
(3, 'Pan-D Capsule', 'Pantoprazole (40mg) + Domperidone (30mg SR)', 'BAT-PND-2024-11', 8, 25, 195.00, '2024-11-01', '2026-11-15', 1, 3),
(4, 'Combiflam Tablet', 'Ibuprofen (400mg) + Paracetamol (325mg)', 'BAT-CMB-2024-08', 210, 40, 46.00, '2024-08-15', '2027-08-15', 1, 3),
(5, 'Azithral 500mg Tablet', 'Azithromycin 500mg', 'BAT-AZI-2024-06', 42, 15, 135.00, '2024-06-12', '2026-10-25', 1, 4),
(6, 'Shelcal 500 Tablet', 'Calcium 500mg + Vitamin D3 250 IU', 'BAT-SHL-2025-02', 6, 20, 142.00, '2025-02-14', '2027-08-14', 1, 5),
(7, 'Benadryl Cough Syrup (100ml)', 'Diphenhydramine HCl + Ammonium Chloride', 'BAT-BND-2025-01', 55, 15, 115.00, '2025-01-20', '2027-01-20', 2, 1),
(8, 'Ascoril-D Plus Syrup (100ml)', 'Dextromethorphan + Phenylephrine', 'BAT-ASC-2025-02', 40, 12, 128.00, '2025-02-18', '2027-08-18', 2, 4),
(9, 'Gelusil Antacid Syrup (200ml)', 'Aluminium Hydroxide + Magnesium Hydroxide', 'BAT-GEL-2024-09', 7, 15, 138.00, '2024-09-01', '2026-11-20', 2, 2),
(10, 'Calpol Paediatric Syrup (60ml)', 'Paracetamol Paediatric 120mg/5ml', 'BAT-CLP-2024-05', 35, 15, 42.00, '2024-05-15', '2026-10-31', 2, 2),
(11, 'Aristozyme Digestive Syrup (200ml)', 'Diastase + Pepsin Enzymes', 'BAT-ARZ-2025-03', 48, 10, 148.00, '2025-03-05', '2027-09-05', 2, 3),
(12, 'Becosules Performance Syrup (225ml)', 'B-Complex + L-Lysine Minerals', 'BAT-BCS-2025-04', 62, 15, 195.00, '2025-04-10', '2027-10-10', 2, 2);
