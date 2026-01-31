package com.example.channel_service;


import com.example.channel_service.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "users")
public interface UserClient {
    @GetMapping("/api/users/{id}")
    UserDto getInfoById (@PathVariable("id") Long id);
}
