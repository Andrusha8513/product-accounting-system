package com.example.profile_service;

import com.example.profile_service.dto.PostProfileDto;
import com.example.profile_service.entity.PostProfile;
import com.example.profile_service.entity.Profile;
import com.example.profile_service.image.Image;
import com.example.profile_service.image.ImageRepository;
import com.example.profile_service.image.ImageService;
import com.example.profile_service.mapper.CommentMapper;
import com.example.profile_service.mapper.ImageMapper;
import com.example.profile_service.repository.PostProfileRepository;
import com.example.profile_service.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final ImageService imageService;
    private final ImageRepository imageRepository;
    private final PostProfileRepository postProfileRepository;
    private final ImageMapper imageMapper;
    private final CommentMapper commentMapper;

    public Profile getProfile(Long id){
        return profileRepository.findById((id))
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден. "));
    }

    @Transactional
    public void addAvatar(Long id, MultipartFile file) throws IOException {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с таким " + id + " не найден"));

        Image image = imageService.toImageEntity(file);
        image.setProfile(profile);
        profile.setAvatarId(image.getId());
        profileRepository.save(profile);
    }

    @Transactional
    public void addPhotos(Long id, List<MultipartFile> files) throws IOException {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с таким " + id + " не найден"));

        List<Image> images = profile.getPhotos();
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                Image image = imageService.toImageEntity(file);
                image.setProfile(profile);
                images.add(image);
            }
        }
        profileRepository.save(profile);
    }

    @Transactional
    public void updateAvatar(Long id, Long newAvatar) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с таким " + id + " не найден"));
        profile.setAvatarId(newAvatar);
        profileRepository.save(profile);
    }

    @Transactional
    public void deletePhoto(Long id, Long photoId) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователя с таким " + id + " не найдено"));

        Image imageFomDeleta = imageRepository.findById(photoId)
                .orElseThrow(() -> new IllegalArgumentException("Фото с таким " + photoId + " не найдно"));

        if (!imageFomDeleta.getProfile().getId().equals(id)) {
            throw new IllegalArgumentException("Фотография не принадлежит пользователю");
        }
        profile.getPhotos().remove(imageFomDeleta);
        imageRepository.delete(imageFomDeleta);
        profileRepository.save(profile);
    }

    @Transactional
    public void savePost(PostProfileDto profileDto){
        PostProfile profile = postProfileRepository.findById(profileDto.getId())
                .orElse(new PostProfile());

        Profile owner = profileRepository.findById(profileDto.getUserId())
                .orElseThrow(() -> new RuntimeException("Профиль для пользователя userId не найден: " + profileDto.getUserId()));


        profile.setProfile(owner);
        profile.setId(profileDto.getId());
        profile.setDescription(profileDto.getDescription());
        profile.setComments(profileDto.getComments().stream().map(commentMapper::toEntity).toList());
        profile.setImages(profileDto.getImages().stream().map(imageMapper::toEntity).toList());
        postProfileRepository.save(profile);
    }

    @Transactional
    public PostProfile updatePost(PostProfileDto profileDto){
        PostProfile profile = postProfileRepository.findById(profileDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Пост не найден: " + profileDto.getId()));

        Profile owner = profileRepository.findById(profileDto.getUserId())
                .orElseThrow(() -> new RuntimeException("Профиль для пользователя userId не найден: " + profileDto.getUserId()));


        profile.setProfile(owner);
        profile.setId(profileDto.getId());
        profile.setDescription(profileDto.getDescription());
        profile.setComments(profileDto.getComments().stream().map(commentMapper::toEntity).toList());
        profile.setImages(profileDto.getImages().stream().map(imageMapper::toEntity).toList());
       return postProfileRepository.save(profile);
    }

    @Transactional
    public void deletePost(Long id){
        postProfileRepository.deleteById(id);
    }

}
