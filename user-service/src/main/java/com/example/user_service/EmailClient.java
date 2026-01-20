package com.example.user_service;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "email-service")
public interface EmailClient {

    @PostMapping("/api/email/code")
    void sendConfirmationCode(@RequestParam String to, @RequestParam String code);
    @PostMapping("/api/email/resetPassCode")
    void sendPasswordResetCode(@RequestParam String to, @RequestParam String code);
}
