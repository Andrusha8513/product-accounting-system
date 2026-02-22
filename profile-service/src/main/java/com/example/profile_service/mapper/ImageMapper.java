package com.example.profile_service.mapper;

import com.example.profile_service.dto.ImageDto;
import com.example.profile_service.entity.ImagePost;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class ImageMapper {
    public ImagePost toEntity(ImageDto dto) {
        ImagePost image = new ImagePost();
//        image.setId(dto.getId());
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
