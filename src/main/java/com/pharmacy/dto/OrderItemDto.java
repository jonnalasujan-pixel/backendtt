package com.pharmacy.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class OrderItemDto {

    @NotNull
    private Long medicineId;

    @NotNull
    @Min(1)
    private Integer quantity;

    private BigDecimal unitPrice;

    public OrderItemDto() {}

    public OrderItemDto(Long medicineId, Integer quantity, BigDecimal unitPrice) {
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Long getMedicineId() { return medicineId; }
    public void setMedicineId(Long medicineId) { this.medicineId = medicineId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}
