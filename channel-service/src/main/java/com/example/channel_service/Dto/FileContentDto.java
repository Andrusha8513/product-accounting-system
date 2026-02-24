package com.example.channel_service.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileContentDto {
    public String originalFileName;
    public String contentType;
    public byte[] content;
}
