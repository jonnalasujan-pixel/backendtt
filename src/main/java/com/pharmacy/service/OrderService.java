package com.pharmacy.service;

import com.pharmacy.dto.OrderItemDto;
import com.pharmacy.dto.OrderRequest;
import com.pharmacy.entity.Medicine;
import com.pharmacy.entity.Order;
import com.pharmacy.entity.OrderItem;
import com.pharmacy.entity.User;
import com.pharmacy.repository.MedicineRepository;
import com.pharmacy.repository.OrderRepository;
import com.pharmacy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Order createOrder(OrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Cannot create order with empty items list");
        }

        User customer = null;
        if (request.getCustomerId() != null) {
            customer = userRepository.findById(request.getCustomerId()).orElse(null);
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "CASH");
        order.setStatus("COMPLETED");

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomSuffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        order.setInvoiceNumber("INV-" + timestamp + "-" + randomSuffix);

        BigDecimal subtotal = BigDecimal.ZERO;

        for (OrderItemDto itemDto : request.getItems()) {
            Medicine medicine = medicineRepository.findById(itemDto.getMedicineId())
                    .orElseThrow(() -> new RuntimeException("Medicine not found with ID: " + itemDto.getMedicineId()));

            if (medicine.getStockQuantity() < itemDto.getQuantity()) {
                throw new RuntimeException("Insufficient stock for " + medicine.getName() +
                        ". Available: " + medicine.getStockQuantity() + ", Requested: " + itemDto.getQuantity());
            }

            // Decrement inventory stock atomically
            medicine.setStockQuantity(medicine.getStockQuantity() - itemDto.getQuantity());
            medicineRepository.save(medicine);

            BigDecimal unitPrice = itemDto.getUnitPrice() != null ? itemDto.getUnitPrice() : medicine.getUnitPrice();
            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            subtotal = subtotal.add(itemTotal);

            OrderItem orderItem = new OrderItem(medicine, itemDto.getQuantity(), unitPrice, itemTotal);
            order.addOrderItem(orderItem);
        }

        order.setTotalAmount(subtotal);

        BigDecimal discount = request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO;
        order.setDiscountAmount(discount);

        BigDecimal taxableAmount = subtotal.subtract(discount).max(BigDecimal.ZERO);
        BigDecimal taxRate = request.getTaxRate() != null ? request.getTaxRate() : BigDecimal.valueOf(0.05);
        BigDecimal taxAmount = taxableAmount.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
        order.setTaxAmount(taxAmount);

        BigDecimal netAmount = taxableAmount.add(taxAmount).setScale(2, RoundingMode.HALF_UP);
        order.setNetAmount(netAmount);

        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    public List<Order> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerIdOrderByOrderDateDesc(customerId);
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Optional<Order> getOrderByInvoiceNumber(String invoiceNumber) {
        return orderRepository.findByInvoiceNumber(invoiceNumber);
    }
}
