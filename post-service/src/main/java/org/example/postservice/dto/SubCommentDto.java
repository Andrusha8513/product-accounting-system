package org.example.postservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubCommentDto {
    private Long id;
    private String text;
    private Long userId;
}
