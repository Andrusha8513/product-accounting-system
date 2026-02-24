package com.example.channel_service.Service.impl;

import com.example.channel_service.Dto.*;
import com.example.channel_service.Mapper.CommunityMapper;
import com.example.channel_service.Model.Community;
import com.example.channel_service.Model.CommunityMember;
import com.example.channel_service.Repository.CommunityMemberRepository;
import com.example.channel_service.Repository.CommunityRepository;
import com.example.channel_service.RolesMember.CommunityRole;
import com.example.channel_service.Service.CommunityService;
import com.example.support_module.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class CommunityServiceImpl implements CommunityService {
    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final CommunityMapper communityMapper;

    public CommunityServiceImpl(CommunityRepository communityRepository,
                                CommunityMemberRepository communityMemberRepository,
                                KafkaTemplate<String , Object> kafkaTemplate, CommunityMapper communityMapper) {
        this.communityRepository = communityRepository;
        this.communityMemberRepository = communityMemberRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.communityMapper = communityMapper;
    }
    @Transactional
    public CommunityDto createCommunity(String name, String description) {
        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        Long userId = customUserDetails.getId();
        Community community = communityRepository.save(Community.builder().description(description)
                .creatorId(userId)
                .build());
        communityMemberRepository.save(CommunityMember.builder()
                .communityId(community.getId())
                .userId(userId).role(CommunityRole.ADMIN).build());

        return communityMapper.toDto(community);

    }
    @Transactional
    public void join(Long communityId) {
        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        Long userId = customUserDetails.getId();
        if (communityMemberRepository.findByCommunityIdAndUserId(communityId, userId).isPresent()) {
            throw new RuntimeException("Вы уже состоите в группе");
        }
        communityRepository.findById(communityId).orElseThrow();
        communityMemberRepository.save(CommunityMember.builder()
                .communityId(communityId)
                .userId(userId).role(CommunityRole.MEMBER).build());
    }

    @Transactional
    public void unJoin(Long communityId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        Long userId = customUserDetails.getId();
        CommunityMember member = communityMemberRepository
                .findByCommunityIdAndUserId(communityId, userId)
                .orElseThrow(() -> new RuntimeException("Вы не участник сообщества"));
        if (member.getRole() == CommunityRole.ADMIN) {
            throw new RuntimeException("Вы не можете покинуть группу, так как вы админ канала xD");
        }

        communityMemberRepository.deleteByCommunityIdAndUserId(communityId, userId);
    }
    @Transactional
    public void changeRole(Long communityId, Long targetUserId, CommunityRole newRole) {
        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        Long adminId = customUserDetails.getId(); // ID того, кто делает запрос

        CommunityMember adminMember = communityMemberRepository
                .findByCommunityIdAndUserId(communityId, adminId)
                .orElseThrow(() -> new RuntimeException("Вы не участник сообщества"));

        if (adminMember.getRole() != CommunityRole.ADMIN) {
            throw new RuntimeException("Вы не админ, чтобы менять роль");
        }
        CommunityMember targetMember = communityMemberRepository
                .findByCommunityIdAndUserId(communityId, targetUserId)
                .orElseThrow(() -> new RuntimeException("Целевой пользователь не найден в сообществе"));

        targetMember.setRole(newRole);
        communityMemberRepository.save(targetMember);
    }
    private FileContentDto convert(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        try {
            return new FileContentDto(file.getOriginalFilename(), file.getContentType(), file.getBytes());
        } catch (IOException e) { throw new RuntimeException(e); }
    }


    /// Работа с постами через KAFKA
    @Transactional
    public void createPostInCommunity(Long communityId, PostDto postDto, MultipartFile f1, MultipartFile f2, MultipartFile f3) {
        validateAccess(communityId , ActionType.CREATE_POST);
        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        Long userId = customUserDetails.getId();
        CommunityMember member = communityMemberRepository
                .findByCommunityIdAndUserId(communityId, userId).orElseThrow(() -> new RuntimeException("Вы не участник сообщества"));
        CommunityPostEventDto event = CommunityPostEventDto.builder()
                .communityId(communityId)
                .userId(userId)
                .postId(postDto.getId())
                .description(postDto.getDescription())
                .parentId(null)
                .action(ActionType.CREATE_POST)
                .file1(convert(f1))
                .file2(convert(f2))
                .file3(convert(f3))
                .actorRole(member.getRole().name())
                .build();
        kafkaTemplate.send("community_posts", event);
    }
    @Transactional
    public void deletePostById(Long communityId , Long postId) {
        validateAccess(communityId , ActionType.DELETE_POST);
        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        Long userId = customUserDetails.getId();
        CommunityMember communityMember = communityMemberRepository.findByCommunityIdAndUserId(communityId, userId)
                .orElseThrow(() -> new RuntimeException("Вы не участник сообщества"));
        if (communityMember.getRole() == CommunityRole.MEMBER) {
            throw new RuntimeException("Вы не имеете прав удалить данный пост");
        }
        CommunityPostEventDto event = CommunityPostEventDto.builder()
                .communityId(communityId)
                .userId(userId)
                .postId(postId)
                .action(ActionType.DELETE_POST)
                .actorRole(communityMember.getRole().name())
                .build();
        kafkaTemplate.send("community_posts", event);
    }
    @Transactional
    public void updatePostInCommunity(Long postId, Long communityId, PostDto postDto, MultipartFile f1, MultipartFile f2, MultipartFile f3) {
        validateAccess(communityId , ActionType.UPDATE_POST);
        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        Long userId = customUserDetails.getId();
        CommunityMember member = communityMemberRepository
                .findByCommunityIdAndUserId(communityId, userId).orElseThrow(() -> new RuntimeException("Вы не участник сообщества"));
        if (member.getRole() == CommunityRole.MEMBER) {
            throw new RuntimeException("Вы не можете редактировать посты так как не являетесь админом/редактором сообщества");
        }
        CommunityPostEventDto event = CommunityPostEventDto.builder()
                .communityId(communityId)
                .userId(userId)
                .postId(postId)
                .description(postDto.getDescription())
                .action(ActionType.UPDATE_POST)
                .file1(convert(f1))
                .file2(convert(f2))
                .file3(convert(f3))
                .actorRole(member.getRole().name())
                .build();
        kafkaTemplate.send("community_posts", event);
    }

    /// Проверка прав
    public void validateAccess(Long communityId, ActionType action) {
        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();

        boolean isGlobalAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isGlobalAdmin) {
            return;
        }
        CommunityMember member = communityMemberRepository
                .findByCommunityIdAndUserId(communityId , customUserDetails.getId())
                .orElseThrow(() -> new RuntimeException("Вы не участник этого сообщества"));
        CommunityRole role = member.getRole();
        boolean hasAccess = switch (action){
            case CREATE_POST  -> (role == CommunityRole.ADMIN || role == CommunityRole.EDITOR);
            case UPDATE_POST ->  (role == CommunityRole.ADMIN || role == CommunityRole.EDITOR);
            case DELETE_POST -> (role == CommunityRole.ADMIN || role == CommunityRole.EDITOR);
            case CREATE_COMMENT ->  (role == CommunityRole.ADMIN || role == CommunityRole.EDITOR || role == CommunityRole.MEMBER);
            case UPDATE_COMMENT -> (role == CommunityRole.ADMIN || role == CommunityRole.EDITOR || role == CommunityRole.MEMBER);
            case DELETE_COMMENT -> (role == CommunityRole.ADMIN || role == CommunityRole.EDITOR || role == CommunityRole.MEMBER);
            case CREATE_REPLY -> (role == CommunityRole.ADMIN || role == CommunityRole.EDITOR || role == CommunityRole.MEMBER);
            case UPDATE_REPLY -> (role == CommunityRole.ADMIN || role == CommunityRole.EDITOR || role == CommunityRole.MEMBER);
            case DELETE_REPLY -> (role == CommunityRole.ADMIN || role == CommunityRole.EDITOR || role == CommunityRole.MEMBER);
        };
        if (!hasAccess) {
            throw new RuntimeException("У вас нету прав доступа");
        }
    }

    /// Работа с комментариями через кафку
    @Transactional
    public void createCommentInPostOnCommunity(Long communityId , Long postId , String text){
        validateAccess(communityId , ActionType.CREATE_COMMENT);
        Authentication authentication =  SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        CommunityMember member = communityMemberRepository
                .findByCommunityIdAndUserId(communityId , customUserDetails.getId()).orElseThrow(() -> new RuntimeException("Вы не участник этого сообщества"));
        CommunityPostEventDto eventDto = CommunityPostEventDto.builder()
                .communityId(communityId)
                .userId(customUserDetails.getId())
                .postId(postId)
                .description(text)
                .action(ActionType.CREATE_COMMENT)
                .actorRole(member.getRole().name())
                .build();
        kafkaTemplate.send("community_posts", eventDto);
    }
    @Transactional
    public void updateCommentInPostOnCommunity(Long postId, Long communityId , Long commentId , String text){
        validateAccess(communityId , ActionType.UPDATE_COMMENT);
        Authentication authentication =  SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        CommunityMember member = communityMemberRepository
                .findByCommunityIdAndUserId(communityId,user.getId())
                .orElseThrow(() -> new RuntimeException("Вы не участник этого сообщества"));

        CommunityPostEventDto eventDto = CommunityPostEventDto.builder()
                .communityId(communityId)
                .userId(user.getId())
                .postId(postId)
                .description(text)
                .action(ActionType.UPDATE_COMMENT)
                .actorRole(member.getRole().name())
                .commentId(commentId)
                .build();
        kafkaTemplate.send("community_posts", eventDto);
    }
    @Transactional
    public void deleteCommentInPostOnCommunity(Long postId, Long communityId , Long commentId){
        validateAccess(communityId , ActionType.DELETE_COMMENT);
        Authentication authentication =  SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        CommunityMember member = communityMemberRepository
                .findByCommunityIdAndUserId(communityId , user.getId())
                .orElseThrow(() -> new RuntimeException("Вы не участник этого сообщества"));
        CommunityPostEventDto eventDto = CommunityPostEventDto.builder()
                .communityId(communityId)
                .userId(user.getId())
                .postId(postId)
                .action(ActionType.DELETE_COMMENT)
                .actorRole(member.getRole().name())
                .commentId(commentId)
                .build();
        kafkaTemplate.send("community_posts", eventDto);
    }


    /// Работа с ответами на комментарии через кафку
    @Transactional
    public void createReplyToCommentInPostOnCommunity(Long postId, Long communityId , Long commentId , String text){
        validateAccess(communityId , ActionType.CREATE_REPLY);
        Authentication authentication =  SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        CommunityMember member = communityMemberRepository
                .findByCommunityIdAndUserId(communityId , user.getId()).orElseThrow(() -> new RuntimeException("Вы не участник этого сообщества"));
        CommunityPostEventDto eventDto = CommunityPostEventDto.builder()
                .communityId(communityId)
                .userId(user.getId())
                .postId(postId)
                .description(text)
                .parentId(commentId)
                .action(ActionType.CREATE_REPLY)
                .actorRole(member.getRole().name())
                .build();
        kafkaTemplate.send("community_posts", eventDto);
    }
    @Transactional
    public void updateReplyToCommentInPostOnCommunity(Long postId, Long communityId , Long commentId ,Long replyId ,String text){
        validateAccess(communityId , ActionType.UPDATE_REPLY);
        Authentication authentication =  SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        CommunityMember member = communityMemberRepository
                .findByCommunityIdAndUserId(communityId , user.getId())
                .orElseThrow(() -> new RuntimeException("Вы не участник этого сообщества"));
        CommunityPostEventDto eventDto = CommunityPostEventDto.builder()
                .communityId(communityId)
                .userId(user.getId())
                .postId(postId)
                .description(text)
                .parentId(commentId)
                .action(ActionType.UPDATE_REPLY)
                .actorRole(member.getRole().name())
                .replyId(replyId)
                .build();
        kafkaTemplate.send("community_posts", eventDto);
    }
    @Transactional
    public void deleteReplyToCommentInPostOnCommunity(Long postId, Long communityId , Long commentId , Long replyId){
        validateAccess(communityId , ActionType.DELETE_REPLY);
        Authentication authentication =  SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        CommunityMember member = communityMemberRepository
                .findByCommunityIdAndUserId(communityId , user.getId()).orElseThrow(() -> new RuntimeException("Вы не участник этого сообщества"));
        CommunityPostEventDto eventDto = CommunityPostEventDto.builder()
                .communityId(communityId)
                .userId(user.getId())
                .postId(postId)
                .parentId(commentId)
                .action(ActionType.DELETE_REPLY)
                .actorRole(member.getRole().name())
                .replyId(replyId)
                .build();
        kafkaTemplate.send("community_posts", eventDto);
    }

    public List<CommunityDto> findAllCommunity() {
        return communityRepository.findAll().stream().map(communityMapper::toDto).toList();
    }


    public CommunityDto getCommunityById(Long communityId) {
        return communityMapper.toDto(communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Такого канала нету")));
    }
}
