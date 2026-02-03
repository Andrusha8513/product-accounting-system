package com.example.channel_service.Service.impl;

import com.example.channel_service.Config.PostClient;
import com.example.channel_service.Config.UserClient;
import com.example.channel_service.Dto.CommunityDto;
import com.example.channel_service.Dto.PostDto;
import com.example.channel_service.Dto.UserDto;
import com.example.channel_service.Mapper.CommunityMapper;
import com.example.channel_service.Model.Community;
import com.example.channel_service.Model.CommunityMember;
import com.example.channel_service.Repository.CommunityMemberRepository;
import com.example.channel_service.Repository.CommunityRepository;
import com.example.channel_service.RolesMember.CommunityRole;
import com.example.channel_service.Service.CommunityService;
import org.springframework.web.multipart.MultipartFile;

public class CommunityServiceImpl implements CommunityService {
    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final PostClient postClient;
    private final UserClient userClient;
    private final CommunityMapper communityMapper;

    public CommunityServiceImpl(CommunityRepository communityRepository,
                                CommunityMemberRepository communityMemberRepository,
                                PostClient postClient, UserClient userClient , CommunityMapper communityMapper) {
        this.communityRepository = communityRepository;
        this.communityMemberRepository = communityMemberRepository;
        this.postClient = postClient;
        this.userClient = userClient;
        this.communityMapper = communityMapper;
    }

    public CommunityDto createCommunity(String name, String description, String email) {
        UserDto userDto = userClient.getUserByEmail(email);
        Community community = communityRepository.save(Community.builder().description(description)
                .creatorId(userDto.getId())
                .build());
        communityMemberRepository.save(CommunityMember.builder()
                .communityId(community.getId())
                .userId(userDto.getId()).role(CommunityRole.ADMIN).build());

        return communityMapper.toDto(community);

    }

    public void join(Long communityId, String email) {
        UserDto userDto = userClient.getUserByEmail(email);
        if (communityMemberRepository.findByCommunityIdAndUserId(communityId, userDto.getId()).isPresent()) {
            throw new RuntimeException("Вы уже состоите в группе");
        }
        communityRepository.findById(communityId).orElseThrow();
        communityMemberRepository.save(CommunityMember.builder()
                .communityId(communityId)
                .userId(userDto.getId()).role(CommunityRole.MEMBER).build());
    }

    public void unJoin(Long communityId, String email) {
        UserDto userDto = userClient.getUserByEmail(email);
        CommunityMember admin = communityMemberRepository
                .findByCommunityIdAndUserId(communityId, userDto.getId()).orElseThrow(() -> new RuntimeException("Вы не участник сообщества"));
        if (communityMemberRepository.findByCommunityIdAndUserId(communityId, userDto.getId()).isPresent()) {
            throw new RuntimeException("Вы уже состоите в группе");
        }
        if (admin.getRole() == CommunityRole.ADMIN) {
            throw new RuntimeException("Вы не можете покинуть группу тк вы админ канала xD");
        }
        communityMemberRepository.deleteByCommunityIdAndUserId(communityId, userDto.getId());
    }

    public void changeRole(Long communityId, Long userId, CommunityRole newRole, String adminEmail) {
        UserDto admin = userClient.getUserByEmail(adminEmail);
        CommunityMember adminMember = communityMemberRepository
                .findByCommunityIdAndUserId(communityId, admin.getId()).orElseThrow(() -> new RuntimeException("Вы не участник сообщества"));
        if (adminMember.getRole() != CommunityRole.ADMIN) {
            throw new RuntimeException("вы не админ чтобы менять роль");
        }
        CommunityMember targetMember = communityMemberRepository.findByCommunityIdAndUserId(communityId, userId).orElseThrow();
        targetMember.setRole(newRole);
        communityMemberRepository.save(targetMember);
    }

    public PostDto createPostInCommunity(Long communityId, PostDto postDto, MultipartFile f1, MultipartFile f2, MultipartFile f3, String email) {
        UserDto userDto = userClient.getUserByEmail(email);
        CommunityMember member = communityMemberRepository
                .findByCommunityIdAndUserId(communityId, userDto.getId()).orElseThrow(() -> new RuntimeException("Вы не участник сообщества"));
        if (member.getRole() == CommunityRole.MEMBER) {
            throw new RuntimeException("Вы не можете создавать посты так как не являетесь админом/редактором сообщества");
        }
        postDto.setCommunityId(communityId);
        postDto.setUserId(userDto.getId());
        return postClient.createPost(postDto , f1 , f2 ,f3 , email);
    }

    public PostDto updatePostInCommunity(Long postId, Long communityId, PostDto postDto, MultipartFile f1, MultipartFile f2, MultipartFile f3, String email) {
        UserDto userDto = userClient.getUserByEmail(email);
        CommunityMember member = communityMemberRepository
                .findByCommunityIdAndUserId(communityId, userDto.getId()).orElseThrow(() -> new RuntimeException("Вы не участник сообщества"));
        if (member.getRole() == CommunityRole.MEMBER) {
            throw new RuntimeException("Вы не можете редактировать посты так как не являетесь админом/редактором сообщества");
        }
        return postClient.updatePost(postId ,  postDto , f1 , f2 ,f3 , email);
    }
    /// Для проверки прав
    public boolean checkPermission(Long communityId, Long userId, String action) {
        return communityMemberRepository.findByCommunityIdAndUserId(communityId , userId)
                .map(communityMember -> {
            if (communityMember.getRole() == CommunityRole.ADMIN) return true;
            if (communityMember.getRole() == CommunityRole.EDITOR) return true;
            return false;
        })
                .orElse(false);
    }
}
