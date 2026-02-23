package com.example.profile_service.mapper;

import com.example.profile_service.dto.ImageDto;
import com.example.profile_service.dto.ImagePostDto;
import com.example.profile_service.entity.ImagePost;
import com.example.profile_service.entity.Image;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class ImageMapper {
    public ImagePost toEntity(ImagePostDto dto) {
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

    public ImageDto toDto(Image image){
        ImageDto imageDto = new ImageDto();
        imageDto.setId(image.getId());
        imageDto.setName(image.getName());
        imageDto.setSize(image.getSize());
        imageDto.setOriginalFileName(image.getOriginalFileName());
        imageDto.setContentType(image.getContentType());
        return imageDto;
    }

    public ImagePostDto toImagePostDto(ImagePost image) {
        ImagePostDto dto = new ImagePostDto();
        dto.setId(image.getId());
        dto.setName(image.getName());
        dto.setOriginalFileName(image.getOriginalFileName());
        dto.setSize(image.getSize());
        dto.setContentType(image.getContentType());
        dto.setPreviewImages(image.isPreviewImages());
        dto.setPostId(image.getPost() != null ? image.getPost().getId() : null);

//        if (image.getBytes() != null) {
//            dto.setBase64(Base64.getEncoder().encodeToString(image.getBytes()));
//        }

        return dto;
    }
}
