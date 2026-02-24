package com.example.channel_service.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPostEventDto {
    public Long communityId;
    public Long userId;
    public Long postId;
    private String description;
    private Long parentId;
    private ActionType action;
    private FileContentDto file1;
    private FileContentDto file2;
    private FileContentDto file3;
    private Long commentId;
    private Long replyId;
    private String actorRole;
}
