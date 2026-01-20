package org.example.postservice.service.impl;

import com.example.user_service.UserRepository;
import com.example.user_service.dto.UserDto;
import org.example.postservice.Model.Image;
import org.example.postservice.Model.Post;
import org.example.postservice.dto.PostDto;
import org.example.postservice.mapper.ImageMapper;
import org.example.postservice.mapper.PostMapper;
import org.example.postservice.repository.PostRepository;
import org.example.postservice.service.PostService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    public PostServiceImpl(PostRepository postRepository, PostMapper postMapper, ImageMapper imageMapper) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
    }

    public List<PostDto> findAllPosts() {
        return postRepository.findAll().stream().map(postMapper::toDto).toList();
    }


    public List<PostDto> findAllPostsByUserId(Long id) {
        return postRepository.findAllPostsByUserId(id).stream().map(postMapper::toDto).toList();
    }


    public PostDto findPostById(Long id) {
        return postMapper.toDto(postRepository.findById(id).orElseThrow());
    }


    public PostDto createPost(PostDto postDto, MultipartFile file1, MultipartFile file2, MultipartFile file3 , UserDto userDto) {
        Post post = postMapper.toEntity(postDto);
        if (userDto != null) {
            post.setUserId(userDto.getId());
        }

        List<MultipartFile> files = new ArrayList<>();
        boolean previewSet = false;
        if (file1 != null) {
            files.add(file1);
        }
        if (file2 != null) {
            files.add(file2);
        }
        if (file3 != null) {
            files.add(file3);
        }
        for (MultipartFile file : files) {
            if (file != null) {
                Image image = new Image();
                image.setName(file.getOriginalFilename());
                image.setOriginalFileName(file.getOriginalFilename());
                image.setSize(file.getSize());
                image.setContentType(file.getContentType());
                image.setPreviewImages(!previewSet);
                previewSet = true;

                try {
                    image.setBytes(file.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                image.setPost(post);
                post.getImages().add(image);
            }
        }
        Post savedPost = postRepository.save(post);
        return postMapper.toDto(savedPost);
    }


    public void deletePostById(Long id) {
        postRepository.deleteById(id);
    }


    public PostDto updatePost(Long id, PostDto postDto, MultipartFile file1, MultipartFile file2, MultipartFile file3) {
        Post post = postRepository.findById(id).orElseThrow();
        post.setDescription(postDto.getDescription());
        post.getImages().clear();

        boolean previewSet = false;
        List<MultipartFile> files = new ArrayList<>();
        if (file1 != null) {
            files.add(file1);
        }
        if (file2 != null) {
            files.add(file2);
        }
        if (file3 != null) {
            files.add(file3);
        }
        for (MultipartFile file : files) {
            if (file != null) {
                Image image = new Image();
                image.setName(file.getOriginalFilename());
                image.setOriginalFileName(file.getOriginalFilename());
                image.setSize(file.getSize());
                image.setContentType(file.getContentType());
                image.setPreviewImages(!previewSet);
                previewSet = true;

                try {
                    image.setBytes(file.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                image.setPost(post);
                post.getImages().add(image);
            }
        }
        Post updatedPost = postRepository.save(post);
        return postMapper.toDto(updatedPost);
    }
}
