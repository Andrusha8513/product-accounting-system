package com.example.profile_service.dto;

import com.example.user_service.dto.PrivetUserProfileDto;
import lombok.AllArgsConstructor;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.postservice.dto.PostDto;
;import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FullProfileDto {
    private PrivetUserProfileDto userDetails;
    private List<PostDto> posts;
}
