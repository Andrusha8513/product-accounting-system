package com.example.api_gateway.jwt;

import com.example.api_gateway.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class TokenData {
    private Long id;
    private String email;
    private String password;
    private Set<Role> roles;
    private Boolean isEnabled;
    private Boolean isAccountNonLocked;
}
