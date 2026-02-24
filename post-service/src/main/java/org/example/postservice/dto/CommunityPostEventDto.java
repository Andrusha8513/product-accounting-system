package org.example.postservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
