package com.example.profile_service.controller;

import com.example.profile_service.Profile;
import com.example.profile_service.ProfileService1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/profile")
public class PfofileController1 {

    private final ProfileService1 profileService1;

    @GetMapping("/myProfile/")
    @PreAuthorize("@securityService.isOwnerForEmail(#email) or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Profile> getMyProfile(@RequestParam String email){
        try {
          Profile profile =  profileService1.getProfile(email);
            return ResponseEntity.ok(profile);
        }catch (Exception e){
            log.info("Причина ошибки " + e);
            return ResponseEntity.badRequest().build();
        }
    }
}
