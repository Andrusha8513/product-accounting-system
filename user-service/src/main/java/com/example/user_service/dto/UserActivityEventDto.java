package com.example.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserActivityEventDto {
    private Long id;
    private Long userId;
    private Long parentId;
    private ActionType action;
    private String entityType;
}
