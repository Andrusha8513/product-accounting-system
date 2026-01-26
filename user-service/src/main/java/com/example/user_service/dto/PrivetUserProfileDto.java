package com.example.user_service.dto;

import com.example.user_service.image.Image;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class PrivetUserProfileDto {
    private Long id;
    private String name;
    private String secondName;
    private String email;
    private LocalDate birthday;
    private String password;
    private Long avatarId;
    private List<Image> images = new ArrayList<>();
}
