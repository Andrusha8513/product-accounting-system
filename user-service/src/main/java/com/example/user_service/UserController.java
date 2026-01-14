package com.example.user_service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RequiredArgsConstructor
@RestController
@RequestMapping("/main")
public class UserController {
    private final UserService userService;


    @PostMapping("/registration")
    public ResponseEntity<?> registrationUser(@RequestBody Users users){
        try {
            userService.createUsers(users);
            return ResponseEntity.ok().build();
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
