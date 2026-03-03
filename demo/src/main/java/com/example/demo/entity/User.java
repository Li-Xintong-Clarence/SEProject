package com.example.demo.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String role;          // CUSTOMER, ADMIN
    private LocalDateTime registrationDate;
    private Boolean isActive;
}
