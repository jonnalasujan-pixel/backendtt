package com.pharmacy.dto;

import com.pharmacy.entity.User;

public class PharmacistSummaryDto {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String address;

    public PharmacistSummaryDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.fullName = user.getFullName();
        this.phone = user.getPhone();
        this.address = user.getAddress();
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
}