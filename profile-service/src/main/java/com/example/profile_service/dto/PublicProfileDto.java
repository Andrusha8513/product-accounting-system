package com.example.profile_service.dto;

import lombok.Data;

@Data
public class PublicProfileDto {
    private Long id;
    private String name;
    private String secondName;
    private String email;
}
