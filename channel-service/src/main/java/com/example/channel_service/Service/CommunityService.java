package com.example.channel_service.Service;

import com.example.channel_service.Dto.ActionType;
import com.example.channel_service.Dto.CommunityDto;
import com.example.channel_service.Dto.FileContentDto;
import com.example.channel_service.Dto.PostDto;
import com.example.channel_service.Model.Community;
import com.example.channel_service.RolesMember.CommunityRole;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CommunityService {
    CommunityDto createCommunity(String name, String description);
    void join(Long communityId);
    void unJoin(Long communityId);
    void changeRole(Long communityId, Long targetUserId, CommunityRole newRole);
    void createPostInCommunity(Long communityId, PostDto postDto, MultipartFile f1, MultipartFile f2, MultipartFile f3);
    void deletePostById(Long communityId , Long postId);
    void updatePostInCommunity(Long postId, Long communityId, PostDto postDto, MultipartFile f1, MultipartFile f2, MultipartFile f3);
    void validateAccess(Long communityId, ActionType action);
    void createCommentInPostOnCommunity(Long communityId , Long postId , String text);
    void updateCommentInPostOnCommunity(Long postId, Long communityId , Long commentId , String text);
    void deleteCommentInPostOnCommunity(Long postId, Long communityId , Long commentId);
    void createReplyToCommentInPostOnCommunity(Long postId, Long communityId , Long commentId , String text);
    void updateReplyToCommentInPostOnCommunity(Long postId, Long communityId , Long commentId ,Long replyId ,String text);
    void deleteReplyToCommentInPostOnCommunity(Long postId, Long communityId , Long commentId , Long replyId);
    List<CommunityDto> findAllCommunity();
    CommunityDto getCommunityById(Long communityId);
}
