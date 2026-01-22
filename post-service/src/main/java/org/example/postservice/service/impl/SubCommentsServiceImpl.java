package org.example.postservice.service.impl;


import org.example.postservice.Model.Comment;
import org.example.postservice.Model.Post;
import org.example.postservice.Model.SubComment;
import org.example.postservice.UserClient;
import org.example.postservice.dto.SubCommentDto;
import org.example.postservice.dto.UserDto;
import org.example.postservice.mapper.CommentMapper;
import org.example.postservice.mapper.SubCommentMapper;
import org.example.postservice.repository.CommentRepository;
import org.example.postservice.repository.SubCommentRepository;
import org.example.postservice.service.SubCommentsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubCommentsServiceImpl implements SubCommentsService {
    private final CommentRepository commentRepository;
    private final SubCommentMapper subCommentMapper;
    private final SubCommentRepository subCommentRepository;
    private final UserClient userClient;

    public SubCommentsServiceImpl(CommentRepository commentRepository ,
                                  SubCommentMapper subCommentMapper,
                                  SubCommentRepository subCommentRepository ,  UserClient userClient) {
        this.commentRepository = commentRepository;
        this.subCommentMapper = subCommentMapper;
        this.subCommentRepository = subCommentRepository;
        this.userClient = userClient;
    }


    public SubCommentDto addSubComment(Long commentId, String text, String email) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        UserDto userDto = userClient.getUserByEmail(email);
        SubComment subComment = new SubComment();
        subComment.setComment(comment);
        subComment.setUserId(userDto.getId());
        subComment.setText(text);
        return subCommentMapper.toDto(subCommentRepository.save(subComment));
    }


    public void deleteSubComment(Long id) {
        subCommentRepository.deleteById(id);
    }


    public SubCommentDto updateSubComment(Long id, String text) {
        SubComment subComment = subCommentRepository.findById(id).orElseThrow();
        subComment.setText(text);
        return subCommentMapper.toDto(subCommentRepository.save(subComment));
    }
}
