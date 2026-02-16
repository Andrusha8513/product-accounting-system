package com.example.profile_service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService1 {
    private final ProfileRepository profileRepository;

    public Profile getProfile(Long id){
        return profileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Профиль не найден с таким id:)" + id + "не найден"));
    }
}
