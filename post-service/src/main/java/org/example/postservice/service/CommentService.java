package org.example.postservice.service;

import org.example.postservice.dto.CommentDto;
import org.example.postservice.dto.PostDto;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CommentService {
    List<CommentDto> getAllComments(Long id,PostDto postDto);
    CommentDto addComment(Long postId, String text , String email);
    void deleteCommentById(Long id);
    CommentDto updateComment(Long id, String text , String email);
}
