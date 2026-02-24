package org.example.postservice.service;
import org.example.postservice.dto.CommunityPostEventDto;
import org.example.postservice.dto.PostDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostService {
    List<PostDto> findAllPosts();
    List<PostDto> findAllPostsByUserId(Long id);
    List<PostDto> getPostsByCommunity(Long communityId);
    PostDto findPostById(Long id);
    PostDto createPost(PostDto postDto , MultipartFile file1 , MultipartFile file2, MultipartFile file3 , String email);
    void deletePostById(Long id , String email);
    PostDto updatePost(Long id,PostDto postDto ,MultipartFile file1, MultipartFile file2, MultipartFile file3, String email);
    void createPostFromKafka(CommunityPostEventDto event);
    void updatePostFromKafka(CommunityPostEventDto event);
    void deletePostFromKafka(CommunityPostEventDto event);
}
