package com.example.user_service.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TestProfileDto {
    private Long id;
    private String name;
    private String secondName;
    private String email;
    private LocalDate birthday;
    private String password;
}
