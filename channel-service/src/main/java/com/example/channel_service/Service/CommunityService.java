package com.example.channel_service.Service;

import com.example.channel_service.Dto.CommunityDto;
import com.example.channel_service.Dto.PostDto;
import com.example.channel_service.Model.Community;
import com.example.channel_service.RolesMember.CommunityRole;
import org.springframework.web.multipart.MultipartFile;

public interface CommunityService {
    CommunityDto createCommunity(String name, String description, String email);
    void join (Long communityId , String email);
    void unJoin(Long communityId , String email);
    void changeRole(Long communityId, Long userId, CommunityRole newRole, String adminEmail);
    PostDto createPostInCommunity(Long communityId, PostDto postDto, MultipartFile f1, MultipartFile f2, MultipartFile f3, String email);
    PostDto updatePostInCommunity(Long postId, Long communityId, PostDto postDto, MultipartFile f1, MultipartFile f2, MultipartFile f3, String email);
    boolean checkPermission(Long communityId, Long userId, String action);
}
