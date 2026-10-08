package com.pharmacy.dto;

import java.math.BigDecimal;

public class DashboardStatsDto {
    private long totalMedicines;
    private long lowStockCount;
    private long expiringSoonCount;
    private long totalOrders;
    private BigDecimal totalRevenue;
    private long pendingPrescriptions;

    public DashboardStatsDto() {}

    public DashboardStatsDto(long totalMedicines, long lowStockCount, long expiringSoonCount, long totalOrders, BigDecimal totalRevenue, long pendingPrescriptions) {
        this.totalMedicines = totalMedicines;
        this.lowStockCount = lowStockCount;
        this.expiringSoonCount = expiringSoonCount;
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
        this.pendingPrescriptions = pendingPrescriptions;
    }

    public long getTotalMedicines() { return totalMedicines; }
    public void setTotalMedicines(long totalMedicines) { this.totalMedicines = totalMedicines; }

    public long getLowStockCount() { return lowStockCount; }
    public void setLowStockCount(long lowStockCount) { this.lowStockCount = lowStockCount; }

    public long getExpiringSoonCount() { return expiringSoonCount; }
    public void setExpiringSoonCount(long expiringSoonCount) { this.expiringSoonCount = expiringSoonCount; }

    public long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public long getPendingPrescriptions() { return pendingPrescriptions; }
    public void setPendingPrescriptions(long pendingPrescriptions) { this.pendingPrescriptions = pendingPrescriptions; }
}
