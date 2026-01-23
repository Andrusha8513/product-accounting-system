package org.example.postservice;

import org.example.postservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface UserClient {
    @GetMapping("/api/users/{id}")
    UserDto getInfoById (@PathVariable("id") Long id);
    @GetMapping("/api/users/email")
    UserDto getUserByEmail(@RequestParam String email);
}
