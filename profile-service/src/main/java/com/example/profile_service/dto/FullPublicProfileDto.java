package com.example.profile_service.dto;

import com.example.user_service.dto.PublicUserProfileDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.postservice.dto.PostDto;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FullPublicProfileDto {
    private PublicUserProfileDto userDetails;
    private List<PostDto> posts;
}
