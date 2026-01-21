package com.example.user_service.controller;

import com.example.user_service.UserService;
import com.example.user_service.dto.JwtAuthenticationDto;
import com.example.user_service.dto.RefreshTokenDto;
import com.example.user_service.dto.UserCredentialsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    @PostMapping("/sing-in")
    public ResponseEntity<JwtAuthenticationDto> singIn(@RequestBody UserCredentialsDto userCredentialsDto) throws AuthenticationException {
        try {
    JwtAuthenticationDto jwtAuthenticationDto = userService.singIn(userCredentialsDto);
    return ResponseEntity.ok(jwtAuthenticationDto);
        }catch (AuthenticationException e){
            throw new RuntimeException("Аутентификация не удалась" + e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public JwtAuthenticationDto refresh(@RequestBody RefreshTokenDto refreshTokenDto) throws Exception {
        return userService.refreshToken(refreshTokenDto);
    }
}
