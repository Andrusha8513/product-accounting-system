package com.example.profile_service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService1 {
    private final ProfileRepository profileRepository;

    public Profile getProfile(String email){
        return profileRepository.findByEmail((email))
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден. "));
    }
}
