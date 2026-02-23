package com.example.profile_service.repository;

import com.example.profile_service.entity.PostProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostProfileRepository extends JpaRepository<PostProfile , Long> {
    Optional<PostProfile> findById(Long id);

}
