package org.example.postservice.service;

import org.example.postservice.dto.CommentDto;
import org.example.postservice.dto.PostDto;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CommentService {
    List<CommentDto> getAllComments(Long id,PostDto postDto);
    CommentDto addComment(Long postId,CommentDto commentDto , String text , String email);
    void deleteCommentById(Long id);
    CommentDto updateComment(Long id,CommentDto commentDto , String text , String username);
}
