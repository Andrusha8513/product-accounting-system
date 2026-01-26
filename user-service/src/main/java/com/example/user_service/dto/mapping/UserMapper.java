package com.example.user_service.dto.mapping;

import com.example.user_service.Users;
import com.example.user_service.dto.JwtAuthenticationDto;
import com.example.user_service.dto.PrivetUserProfileDto;
import com.example.user_service.dto.PublicUserProfileDto;
import com.example.user_service.dto.UserRegistrationDTO;
import org.springframework.stereotype.Component;

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
        dto.setEmail(users.getEmail());
        dto.setName(users.getName());
        dto.setSecondName(users.getSecondName());
        dto.setBirthday(users.getBirthDay());
        dto.setPassword(users.getPassword());
        dto.setImages(users.getPhotos());
        return dto;
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
