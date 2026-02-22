package org.example.postservice.mapper;

import lombok.RequiredArgsConstructor;
import org.example.postservice.Model.Comment;
import org.example.postservice.Model.Post;
import org.example.postservice.dto.PostDto;
import org.example.postservice.dto.PostProfileDto;
import org.springframework.jdbc.core.metadata.PostgresCallMetaDataProvider;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostMapper {
    private final ImageMapper imageMapper;
    private final CommentMapper commentMapper;

    public PostDto toDto(Post post) {
        PostDto postDto = new PostDto();
        postDto.setId(post.getId());
        postDto.setDescription(post.getDescription());
        if (post.getUserId() != null) {
            postDto.setUserId(post.getUserId());
        }
        if (post.getImages() != null) {
            postDto.setImages(post.getImages().stream().map(imageMapper::toDto).toList());
        }
        return postDto;
    }

    public Post toEntity(PostDto postDto) {
        Post post = new Post();
        post.setId(postDto.getId());
        post.setDescription(postDto.getDescription());
        if (postDto.getUserId() != null) {
            post.setUserId(postDto.getUserId());
        }
        return post;
    }

    public PostProfileDto toProfileDto(Post post) {
        PostProfileDto profile = new PostProfileDto();
        profile.setId(post.getId());
        profile.setUserId(post.getUserId());
        profile.setDescription(post.getDescription());
        if (post.getComments() != null) {
            profile.setComments(post.getComments().stream().map(commentMapper::toDto).toList());
        }
        if (post.getImages() != null) {
            profile.setImages(post.getImages().stream().map(imageMapper::toDto).toList());
        }
        return profile;
    }
}
