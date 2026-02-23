package com.example.profile_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ProfileSummaryDto {
    private Long id;
    private String name;
    private String secondName;
    private String email;
    private LocalDate birthday;
}
