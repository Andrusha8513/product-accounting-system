package com.example.profile_service.mapper;

import com.example.profile_service.dto.PostDto;
import com.example.profile_service.entity.PostProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostMapper {
    private final ImageMapper imageMapper;
    private final CommentMapper commentMapper;

    public PostDto toDto(PostProfile profile){
        PostDto postDto = new PostDto();
        postDto.setId(profile.getId());
        postDto.setDescription(profile.getDescription());
        postDto.setUserId(postDto.getUserId());
        postDto.setImages(profile.getImages().stream().map(imageMapper::toImagePostDto).toList());
        postDto.setComments(profile.getComments().stream().map(commentMapper::toDto).toList());
        return postDto;
    }

}
