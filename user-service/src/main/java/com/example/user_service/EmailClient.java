package com.example.user_service;

import com.example.user_service.dto.EmailRequestDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "email-service")
public interface EmailClient {

    @PostMapping("/api/email/code")
    void sendConfirmationCode(@RequestBody EmailRequestDto emailRequestDto);
    @PostMapping("/api/email/resetPassCode")
    void sendPasswordResetCode(@RequestBody EmailRequestDto emailRequestDto);
}
