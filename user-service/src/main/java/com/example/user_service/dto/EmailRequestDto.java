package com.example.user_service.dto;

import lombok.Data;

@Data
public class EmailRequestDto {
    private String to;
    private String code;
}
