package com.example.channel_service.Mapper;

import com.example.channel_service.Dto.CommunityMemberDto;
import com.example.channel_service.Model.CommunityMember;
import org.springframework.stereotype.Component;

@Component
public class CommunityMemberMapper {
    public CommunityMemberDto toDto(CommunityMember communityMember) {
        CommunityMemberDto communityMemberDto = new CommunityMemberDto();
        communityMemberDto.setId(communityMember.getId());

        if (communityMember.getCommunityId() != null) {
            communityMemberDto.setCommunityId(communityMember.getCommunityId());
        }
        if (communityMember.getUserId() != null) {
            communityMemberDto.setUserId(communityMember.getUserId());
        }

        communityMemberDto.setRole(communityMember.getRole());
        return communityMemberDto;
    }
    public CommunityMember toEntity(CommunityMemberDto communityMemberDto) {
        CommunityMember communityMember = new CommunityMember();
        communityMember.setId(communityMemberDto.getId());

        if (communityMemberDto.getCommunityId() != null) {
            communityMember.setCommunityId(communityMemberDto.getCommunityId());
        }
        if (communityMemberDto.getUserId() != null) {
            communityMember.setUserId(communityMemberDto.getUserId());
        }

        communityMember.setRole(communityMemberDto.getRole());
        return communityMember;
    }
}
