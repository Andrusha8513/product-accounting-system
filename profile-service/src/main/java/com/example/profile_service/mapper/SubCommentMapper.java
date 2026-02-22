package com.example.profile_service.mapper;


import com.example.profile_service.dto.SubCommentDto;
import com.example.profile_service.entity.SubComment;
import org.springframework.stereotype.Component;

@Component
public class SubCommentMapper {
    public SubCommentDto toDto(SubComment subComment) {
        SubCommentDto subCommentDto = new SubCommentDto();
        subCommentDto.setId(subComment.getId());
        subCommentDto.setText(subComment.getText());
        if (subComment.getUserId() != null) {
            subCommentDto.setUserId(subComment.getUserId());
        }
        return subCommentDto;
    }
    public SubComment toEntity(SubCommentDto subCommentDto) {
        SubComment subComment = new SubComment();
        subComment.setId(subCommentDto.getId());
        subComment.setText(subCommentDto.getText());
        return subComment;
    }
}
