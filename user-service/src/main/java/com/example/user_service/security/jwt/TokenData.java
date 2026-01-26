package com.example.user_service.security.jwt;

import com.example.user_service.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class TokenData {
    private String email;
    private String password;
    private Set<Role> roles;
    private Boolean isEnabled;
    private Boolean isAccountNonLocked;
}
