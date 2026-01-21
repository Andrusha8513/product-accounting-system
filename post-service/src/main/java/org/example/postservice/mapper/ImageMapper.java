package org.example.postservice.mapper;

import org.example.postservice.Model.Image;
import org.example.postservice.dto.ImageDto;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class ImageMapper {
    public ImageDto toDto(Image image) {
        ImageDto dto = new ImageDto();
        dto.setId(image.getId());
        dto.setName(image.getName());
        dto.setOriginalFileName(image.getOriginalFileName());
        dto.setSize(image.getSize());
        dto.setContentType(image.getContentType());
        dto.setPreviewImages(image.isPreviewImages());
        dto.setPostId(image.getPost() != null ? image.getPost().getId() : null);

        if (image.getBytes() != null) {
            dto.setBase64(Base64.getEncoder().encodeToString(image.getBytes()));
        }

        return dto;
    }

    public Image toEntity(ImageDto dto) {
        Image image = new Image();
        image.setId(dto.getId());
        image.setName(dto.getName());
        image.setOriginalFileName(dto.getOriginalFileName());
        image.setSize(dto.getSize());
        image.setContentType(dto.getContentType());
        image.setPreviewImages(dto.isPreviewImages());

        if (dto.getBase64() != null) {
            image.setBytes(Base64.getDecoder().decode(dto.getBase64()));
        }

        return image;
    }
}
