package org.example.postservice.dto;

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
