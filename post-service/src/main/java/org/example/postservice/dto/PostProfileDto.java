package org.example.postservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class PostProfileDto {
    private Long id;
    private String description;
    private Long userId;
    private List<ImageDto> images;
    private List<CommentDto> comments;
    private ProfileActionType actionType;
}
