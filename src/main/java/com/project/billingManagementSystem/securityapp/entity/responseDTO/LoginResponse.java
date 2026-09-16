package com.project.billingManagementSystem.securityapp.entity.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {

    private String message;
    private String username;
    private String token;
}
