package com.example.profile_service.dto;


import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ProfileResponseDto {
 private Long id;
 private String name;
 private String secondName;
 private String email;
 private LocalDate birthday;
 private Long avatarId;
 private List<ImageDto> photos;
 private List<ImagePostDto> photosPost;
 private List<PostDto> posts;
}
