package com.example.profile_service.repository;

import com.example.profile_service.entity.ImagePost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageRepository extends JpaRepository<ImagePost , Long> {
}
