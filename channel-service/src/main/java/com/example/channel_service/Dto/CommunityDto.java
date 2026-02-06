package com.example.channel_service.Dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommunityDto {
    private Long id;
    private String name;
    private String description;
    private Long creatorId;
    private LocalDateTime dateOfCreated;
}
