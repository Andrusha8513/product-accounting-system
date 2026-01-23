package org.example.postservice.mapper;

import org.example.postservice.Model.Post;
import org.example.postservice.dto.PostDto;
import org.springframework.jdbc.core.metadata.PostgresCallMetaDataProvider;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {
    private final ImageMapper imageMapper;
    public PostMapper(ImageMapper imageMapper) {
        this.imageMapper = imageMapper;
    }
    public PostDto toDto(Post post){
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
    public Post toEntity(PostDto postDto){
        Post post = new Post();
        post.setId(postDto.getId());
        post.setDescription(postDto.getDescription());
        if (postDto.getUserId() != null) {
            post.setUserId(postDto.getUserId());
        }
        return post;
    }
}
