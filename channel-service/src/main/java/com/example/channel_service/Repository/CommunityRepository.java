package com.example.channel_service.Repository;

import com.example.channel_service.Model.Community;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommunityRepository extends JpaRepository<Community, Long> {
    List<Community> findAllByCreatorId(Long creatorId);
    Optional<Community> findByName(String name);
}
