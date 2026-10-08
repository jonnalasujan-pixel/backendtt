package com.pharmacy.dto;

import javax.validation.constraints.NotBlank;

public class GoogleLoginRequest {

    @NotBlank
    private String credential;

    public String getCredential() { return credential; }
    public void setCredential(String credential) { this.credential = credential; }
}