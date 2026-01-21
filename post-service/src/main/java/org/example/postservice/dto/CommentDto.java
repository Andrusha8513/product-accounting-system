package org.example.postservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CommentDto {
    private Long id;
    private String text;
    private Long userId;
    private PostDto post;
    private List<SubCommentDto> subcomments;
}
