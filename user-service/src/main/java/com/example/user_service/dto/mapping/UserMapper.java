package com.example.user_service.dto.mapping;

import com.example.user_service.Users;
import com.example.user_service.dto.*;
import com.example.user_service.image.Image;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public UserRegistrationDTO toDto(Users users){
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setEmail(users.getEmail());
        dto.setName(users.getName());
        dto.setSecondName(users.getSecondName());
        dto.setBirthDay(users.getBirthDay());
        dto.setRoles(users.getRoles());
//        dto.setEnabled(users.getEnable());
        dto.setPassword(users.getPassword());
        return dto;
    }
    public Users toEntity(UserRegistrationDTO userDto){
        Users users = new Users();
        users.setEmail(userDto.getEmail());
        users.setName(userDto.getName());
        users.setSecondName(userDto.getSecondName());
        users.setBirthDay(userDto.getBirthDay());
        users.setRoles(userDto.getRoles());
//        users.setEnable(userDto.isEnabled());
        users.setPassword(userDto.getPassword());
        return users;
    }

    public PrivetUserProfileDto toPrivetProfielDto(Users users){
        PrivetUserProfileDto dto = new PrivetUserProfileDto();
        dto.setId(users.getId());
        dto.setName(users.getName());
        dto.setSecondName(users.getSecondName());
        dto.setEmail(users.getEmail());
        dto.setBirthday(users.getBirthDay());
        dto.setPassword(users.getPassword());
        dto.setAvatarId(users.getAvatarId());
        List<ImageDto> imageDtos = users.getPhotos().stream()
                .map(this::toDtoImages)
                .collect(Collectors.toList());
        dto.setImages(imageDtos);
        return dto;
    }

    public ImageDto toDtoImages(Image image){
        ImageDto imageDto = new ImageDto();

        imageDto.setId(image.getId());
        imageDto.setName(image.getName());
        imageDto.setOriginalFileName(image.getOriginalFileName());
        imageDto.setContentType(image.getContentType());
        imageDto.setSize(image.getSize());
        imageDto.setUrl("/image/" + image.getId());

        return imageDto;
    }


    public PublicUserProfileDto toPublicProfileDto(Users users) {
        PublicUserProfileDto dto = new PublicUserProfileDto();
        dto.setName(users.getName());
        dto.setSecondName(users.getSecondName());
        dto.setEmail(users.getEmail());
        dto.setBirthday(users.getBirthDay());
        dto.setAvatarId(users.getAvatarId());
        dto.setImages(users.getPhotos());
        return dto;
    }
}
