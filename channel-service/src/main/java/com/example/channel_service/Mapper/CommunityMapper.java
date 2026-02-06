package com.example.channel_service.Mapper;

import com.example.channel_service.Dto.CommunityDto;
import com.example.channel_service.Model.Community;
import org.springframework.stereotype.Component;

@Component
public class CommunityMapper {
    public CommunityDto toDto(Community community) {
        CommunityDto communityDto = new CommunityDto();
        communityDto.setId(community.getId());
        communityDto.setName(community.getName());
        communityDto.setDescription(community.getDescription());
        if (community.getCreatorId() != null) {
            communityDto.setCreatorId(community.getCreatorId());
        }
        communityDto.setDateOfCreated(community.getDateOfCreated());
        return communityDto;
    }
    public Community toEntity(CommunityDto communityDto) {
        Community community = new Community();
        community.setId(communityDto.getId());
        community.setName(communityDto.getName());
        community.setDescription(communityDto.getDescription());
        if (communityDto.getCreatorId() != null) {
            community.setCreatorId(communityDto.getCreatorId());
        }
        community.setDateOfCreated(communityDto.getDateOfCreated());
        return community;
    }
}
