package com.example.profile_service.mapper;

import com.example.profile_service.dto.ProfileResponseDto;
import com.example.profile_service.entity.ImagePost;
import com.example.profile_service.entity.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileMapper {

    private final ImageMapper imageMapper;
    private final PostMapper postMapper;

    public ProfileResponseDto toDto(Profile profile){
        ProfileResponseDto profileResponseDto = new ProfileResponseDto();
        profileResponseDto.setId(profile.getId());
        profileResponseDto.setName(profile.getName());
        profileResponseDto.setSecondName(profile.getSecondName());
        profileResponseDto.setEmail(profile.getEmail());
        profileResponseDto.setBirthday(profile.getBirthday());
        profileResponseDto.setAvatarId(profile.getAvatarId());
        profileResponseDto.setPhotos(profile.getPhotos().stream().map(imageMapper::toDto).toList());
        profileResponseDto.setPosts(profile.getPostProfiles().stream().map(postMapper::toDto).toList());

        return profileResponseDto;
    }
}
