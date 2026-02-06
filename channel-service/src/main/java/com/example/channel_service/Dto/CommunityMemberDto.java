package com.example.channel_service.Dto;

import com.example.channel_service.RolesMember.CommunityRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommunityMemberDto {
    private Long id;
    private Long communityId;
    private Long userId;
    private CommunityRole role;
}
