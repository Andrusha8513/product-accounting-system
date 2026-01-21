package org.example.postservice.service;

import org.example.postservice.Model.SubComment;
import org.example.postservice.dto.SubCommentDto;
import org.springframework.stereotype.Component;

@Component
public interface SubCommentsService {
    SubCommentDto addSubComment(Long postId , String text , Long userId);
    void deleteSubComment(Long id);
    SubCommentDto updateSubComment(Long id , String text);
}
