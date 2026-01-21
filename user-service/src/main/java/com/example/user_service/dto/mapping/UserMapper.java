package com.example.user_service.dto.mapping;

import com.example.user_service.Users;
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
}
