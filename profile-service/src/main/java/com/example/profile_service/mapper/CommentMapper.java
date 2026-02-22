package com.example.profile_service.mapper;

import com.example.profile_service.dto.CommentDto;
import com.example.profile_service.entity.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public Comment toEntity(CommentDto commentDto) {
        Comment comment = new Comment();
//        comment.setId(commentDto.getId());
        comment.setText(commentDto.getText());
        return comment;
    }
}
