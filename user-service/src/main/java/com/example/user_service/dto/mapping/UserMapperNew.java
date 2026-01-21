package com.example.user_service.dto.mapping;

import com.example.user_service.Users;
import com.example.user_service.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapperNew {
    public UserDto toDto(Users users) {
        UserDto userDto = new UserDto();
        userDto.setId(users.getId());
        userDto.setSecondName(users.getSecondName());
        userDto.setEmail(users.getEmail());
        return userDto;
    }
    public Users toEntity(UserDto userDto) {
        Users users = new Users();
        users.setId(userDto.getId());
        users.setSecondName(userDto.getSecondName());
        users.setEmail(userDto.getEmail());
        return users;
    }
}
