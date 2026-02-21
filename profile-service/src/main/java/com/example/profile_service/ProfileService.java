package com.example.profile_service;

import com.example.profile_service.image.Image;
import com.example.profile_service.image.ImageRepository;
import com.example.profile_service.image.ImageService;
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
        imageRepository.save(image);
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
                .orElseThrow(() -> new IllegalArgumentException("Фото с таким " + id + " не найдно"));

        if (!imageFomDeleta.getProfile().getId().equals(id)) {
            throw new IllegalArgumentException("Фотография не принадлежит пользователю");
        }
        profile.getPhotos().remove(imageFomDeleta);
        imageRepository.delete(imageFomDeleta);
        profileRepository.save(profile);
    }
}
