package com.example.channel_service.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageDto {
    private Long id;
    private String name;
    private Long size;
    private String contentType;
}
