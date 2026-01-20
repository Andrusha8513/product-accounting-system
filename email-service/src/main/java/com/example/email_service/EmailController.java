package com.example.email_service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/email")
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/code")
    public ResponseEntity<?> sendConfirmationCode(@RequestParam String to, @RequestParam String code) {
        try {
            emailService.sendConfirmationEmail(to, code);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resetPassCode")
    public ResponseEntity<?> sendPasswordResetCode(@RequestParam String to , @RequestParam String code){
        try {
            emailService.sendPasswordResetCode(to , code);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
