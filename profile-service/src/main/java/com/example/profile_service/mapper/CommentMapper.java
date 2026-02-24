package com.example.profile_service.mapper;

import com.example.profile_service.dto.CommentDto;
import com.example.profile_service.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final SubCommentMapper subCommentMapper;

    public Comment toEntity(CommentDto commentDto) {
        Comment comment = new Comment();
//        comment.setId(commentDto.getId());
        comment.setText(commentDto.getText());
        return comment;
    }

    public CommentDto toDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setUserId(comment.getUserID());
        if (comment.getPost() != null) {
            commentDto.setSubcomments(comment.getSubComments().stream().map(subCommentMapper::toDto).toList());
        }
        return commentDto;
    }
}
