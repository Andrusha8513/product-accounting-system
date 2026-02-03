package com.example.channel_service.Repository;

import com.example.channel_service.Model.CommunityMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommunityMemberRepository extends JpaRepository<CommunityMember,Long> {
    Optional<CommunityMember> findByCommunityIdAndUserId(Long communityId, Long userId);
    List<CommunityMember> findAllByCommunityId(Long communityId);
    void deleteByCommunityIdAndUserId(Long communityId , Long userId);
}
