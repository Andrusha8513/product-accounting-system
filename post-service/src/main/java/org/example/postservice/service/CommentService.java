package org.example.postservice.service;

import org.example.postservice.dto.CommentDto;
import org.example.postservice.dto.CommunityPostEventDto;
import org.example.postservice.dto.PostDto;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CommentService {
    List<CommentDto> getAllComments(Long id);
    CommentDto addComment(Long postId, String text , String email);
    void deleteCommentById(Long id , String email);
    CommentDto updateComment(Long id, String text , String email);
    void createCommentFromKafka(CommunityPostEventDto eventDto);
    void updateCommentFromKafka(CommunityPostEventDto eventDto);
    void deleteCommentFromKafka(CommunityPostEventDto eventDto);
}
