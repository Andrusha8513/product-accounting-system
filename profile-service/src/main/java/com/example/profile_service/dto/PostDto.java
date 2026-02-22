package com.example.profile_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostDto {
    private Long id;
    private String description;
    private Long userId;
    private List<ImagePostDto> images;
    private List<CommentDto> comments;
    private Long communityId;
}
