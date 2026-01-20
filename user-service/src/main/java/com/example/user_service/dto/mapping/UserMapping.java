package com.example.user_service.dto.mapping;

import com.example.user_service.Users;
import com.example.user_service.dto.UserRegistrationDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapping {
    UserRegistrationDTO toDto(Users users);
}
