package org.example.postservice.service;

import org.example.postservice.Model.SubComment;
import org.example.postservice.dto.CommunityPostEventDto;
import org.example.postservice.dto.SubCommentDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SubCommentsService {
    SubCommentDto addSubComment(Long commentId , String text , String email);
    void deleteSubComment(Long id , String email);
    SubCommentDto updateSubComment(Long id , String text , String email);
    void createSubCommentFromKafka(CommunityPostEventDto eventDto);
    void updateSubCommentFromKafka(CommunityPostEventDto eventDto);
    void deleteSubCommentFromKafka(CommunityPostEventDto eventDto);
}
