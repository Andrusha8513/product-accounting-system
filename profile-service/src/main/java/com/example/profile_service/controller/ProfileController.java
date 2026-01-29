package com.example.profile_service.controller;
import org.springframework.core.io.Resource;
import com.example.profile_service.ProfileService;
import com.example.profile_service.dto.FullProfileDto;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/profile")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/my-profile")
    public ResponseEntity<FullProfileDto> getMyProfile(@RequestParam String email) {
        try {
            FullProfileDto fullProfileDto = profileService.getMyProfile(email);
            return ResponseEntity.ok(fullProfileDto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
