package com.example.channel_service.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostDto {
    private Long id;
    private String description;
    private Long userId;       // Автор
    private Long communityId;  // ID группы (ВАЖНО)
    private List<ImageDto> images;
}
