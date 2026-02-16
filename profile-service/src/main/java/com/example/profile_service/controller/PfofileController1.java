package com.example.profile_service.controller;

import com.example.profile_service.Profile;
import com.example.profile_service.ProfileService1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/profile")
public class PfofileController1 {

    private final ProfileService1 profileService1;

    @GetMapping("/myProfile/{id}")
    @PreAuthorize("@securityService.isOwner(#id) or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Profile> getMyProfile(@PathVariable Long id){
        try {
          Profile profile =  profileService1.getProfile(id);
            return ResponseEntity.ok(profile);
        }catch (Exception e){
            log.info("Причина ошибки " + e);
            return ResponseEntity.badRequest().build();
        }
    }
}
