package com.example.user_service.dto.mapping;

import com.example.user_service.Users;
import com.example.user_service.dto.*;
import org.springframework.stereotype.Component;


@Component
public class UserMapper {
    public UserRegistrationDTO toDto(Users users){
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setEmail(users.getEmail());
        dto.setName(users.getName());
        dto.setSecondName(users.getSecondName());
        dto.setBirthDay(users.getBirthDay());
        dto.setPassword(users.getPassword());
        return dto;
    }
    public Users toEntity(UserRegistrationDTO userDto){
        Users users = new Users();
        users.setEmail(userDto.getEmail());
        users.setName(userDto.getName());
        users.setSecondName(userDto.getSecondName());
        users.setBirthDay(userDto.getBirthDay());
        users.setPassword(userDto.getPassword());
        return users;
    }

    public TestProfileDto toTestProfileDto(Users users){
        TestProfileDto dto = new TestProfileDto();
        dto.setId(users.getId());
        dto.setName(users.getName());
        dto.setSecondName(users.getSecondName());
        dto.setEmail(users.getEmail());
        dto.setBirthday(users.getBirthDay());
        dto.setPassword(users.getPassword());
        return dto;
    }
}
