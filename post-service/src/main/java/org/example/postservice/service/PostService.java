package org.example.postservice.service;

import com.example.user_service.dto.UserDto;
import org.example.postservice.dto.PostDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostService {
    List<PostDto> findAllPosts();
    List<PostDto> findAllPostsByUserId(Long id);
    PostDto findPostById(Long id);
    PostDto createPost(PostDto postDto , MultipartFile file1 , MultipartFile file2, MultipartFile file3 , UserDto userDto);
    void deletePostById(Long id);
    PostDto updatePost(Long id,PostDto postDto ,MultipartFile file1, MultipartFile file2, MultipartFile file3);
}
