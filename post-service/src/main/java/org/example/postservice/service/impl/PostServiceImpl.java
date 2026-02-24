package org.example.postservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.postservice.Model.Image;
import org.example.postservice.Model.Post;
import org.example.postservice.Model.UserCache;
import org.example.postservice.dto.*;
//import org.example.postservice.dto.UserActivityEventDto;
import org.example.postservice.mapper.PostMapper;
import org.example.postservice.repository.PostRepository;
import org.example.postservice.repository.UserCacheRepository;
import org.example.postservice.service.PostService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserCacheRepository userCacheRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    public PostServiceImpl(PostRepository postRepository, PostMapper postMapper,
                           UserCacheRepository userCacheRepository,
                           KafkaTemplate<String, Object> kafkaTemplate) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.userCacheRepository = userCacheRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public List<PostDto> findAllPosts() {
        return postRepository.findAll().stream().map(postMapper::toDto).toList();
    }


    public List<PostDto> findAllPostsByUserId(Long id) {
        return postRepository.findAllPostsByUserId(id).stream().map(postMapper::toDto).toList();
    }

    public List<PostDto> getPostsByCommunity(Long communityId) {
        return postRepository.findAllByCommunityId(communityId).stream().map(postMapper::toDto).toList();
    }


    public PostDto findPostById(Long id) {
        return postMapper.toDto(postRepository.findById(id).orElseThrow());
    }

    @Transactional
    public PostDto createPost(PostDto postDto, MultipartFile file1, MultipartFile file2,
                              MultipartFile file3 , String email) {
        if (postDto.getCommunityId() != null){
            throw new RuntimeException("нет доступа");
        }
        UserCache userCache = userCacheRepository.findByEmail(email).orElseThrow();
        Post post = postMapper.toEntity(postDto);
        post.setCommunityId(postDto.getCommunityId());
        if (userCache != null) {
            post.setUserId(userCache.getId());
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
//        kafkaTemplate.send("post-events", new UserActivityEventDto(savedPost.getId(),
//                savedPost.getUserId(),null, ActionType.CREATE,"POST"));
        return postMapper.toDto(savedPost);
    }

    @Transactional
    public void deletePostById(Long id , String email) {
        Post post = postRepository.findById(id).orElseThrow();
        if (post.getCommunityId() != null) {
            throw new RuntimeException("нет доступа");
        }
//        kafkaTemplate.send("post-events", new UserActivityEventDto(id , post.getUserId(),
//                null,ActionType.DELETE, "POST"));
        postRepository.deleteById(id);
    }

    @Transactional
    public PostDto updatePost(Long id, PostDto postDto, MultipartFile file1, MultipartFile file2, MultipartFile file3, String email) {
        if (postDto.getCommunityId() != null){
            throw new RuntimeException("нет доступа");
        }
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
//        kafkaTemplate.send("post-events" , new UserActivityEventDto(updatedPost.getId() ,
//                updatedPost.getUserId(),null ,ActionType.UPDATE, "POST"));
        return postMapper.toDto(updatedPost);
    }

    @Transactional
    public void createPostFromKafka(CommunityPostEventDto event) {
        Post post  = new Post();
        boolean isPriviliged = CommunityRole.ADMIN.name().equalsIgnoreCase(event.getActorRole()) || CommunityRole.EDITOR.name().equalsIgnoreCase(event.getActorRole());
        if (!isPriviliged) {
            log.error("Отказ доступа");
            return;
        }
        post.setDescription(event.getDescription());
        post.setCommunityId(event.getCommunityId());
        post.setUserId(event.getUserId());
        if (event.getFile1() != null) {
            post.getImages().add(buildImage(event.getFile1(),post , true));
        }
        if (event.getFile2() != null) {
            post.getImages().add(buildImage(event.getFile2(),post , true));
        }
        if (event.getFile3() != null) {
            post.getImages().add(buildImage(event.getFile3(),post , true));
               }
        postRepository.save(post);

    }
    @Transactional
    public void updatePostFromKafka(CommunityPostEventDto event) {
        Post post = postRepository.findById(event.getPostId()).orElseThrow();
        boolean isPriviliged = CommunityRole.ADMIN.name().equalsIgnoreCase(event.getActorRole())  || CommunityRole.EDITOR.name().equalsIgnoreCase(event.getActorRole());
        if (!isPriviliged) {
            log.error("отказ доступа");
        }
        post.setDescription(event.getDescription());
        if  (event.getFile1() != null || event.getFile2() != null || event.getFile3() != null) {
            post.getImages().clear();
        }
        if (event.getFile1() != null) {
            post.getImages().add(buildImage(event.getFile1(),post , true));
        }
        if (event.getFile2() != null) {
            post.getImages().add(buildImage(event.getFile2(),post , true));
        }
        if (event.getFile3() != null) {
            post.getImages().add(buildImage(event.getFile3(),post , true));
        }
        postRepository.save(post);
    }
    @Transactional
    public void deletePostFromKafka(CommunityPostEventDto event) {
        Post post = postRepository.findById(event.getPostId()).orElseThrow();
        boolean isPriviliged = CommunityRole.ADMIN.name().equalsIgnoreCase(event.getActorRole()) || CommunityRole.EDITOR.name().equalsIgnoreCase(event.getActorRole());
        if (isPriviliged) {
            postRepository.delete(post);
        }else {
            log.error("Отказ в доступе");
        }
    }
    private Image buildImage(FileContentDto fileDto, Post post, boolean isPreview) {
        Image image = new Image();
        image.setName(fileDto.getOriginalFileName());
        image.setContentType(fileDto.getContentType());
        image.setSize((long) fileDto.getContent().length);
        image.setBytes(fileDto.getContent());
        image.setPreviewImages(isPreview);
        image.setPost(post);
        return image;
    }
}
