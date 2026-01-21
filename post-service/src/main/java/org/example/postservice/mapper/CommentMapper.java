package org.example.postservice.mapper;

import org.example.postservice.Model.Comment;
import org.example.postservice.dto.CommentDto;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    private final SubCommentMapper subCommentMapper;
    public CommentMapper(SubCommentMapper subCommentMapper) {
        this.subCommentMapper = subCommentMapper;
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
    public Comment toEntity(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setText(commentDto.getText());
        return comment;
    }
}
