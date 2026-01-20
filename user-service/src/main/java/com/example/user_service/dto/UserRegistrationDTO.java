package com.example.user_service.dto;

import com.example.user_service.Role;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UserRegistrationDTO {
    private String name;
    private String secondName;
    private String password;
    private String email;
    private LocalDate birthDay;
    private boolean enabled;
    private Set<Role> roles;
}
