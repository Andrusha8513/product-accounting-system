package org.example.postservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.postservice.CommunityClient;
import org.example.postservice.Model.Image;
import org.example.postservice.Model.Post;
import org.example.postservice.Model.UserCache;
import org.example.postservice.dto.ActionType;
import org.example.postservice.dto.PostDto;
//import org.example.postservice.dto.UserActivityEventDto;
import org.example.postservice.dto.PostProfileDto;
import org.example.postservice.dto.ProfileActionType;
import org.example.postservice.kafka.ProfileKafkaProducer;
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
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserCacheRepository userCacheRepository;
    private final CommunityClient communityClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ProfileKafkaProducer profileKafkaProducer;


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

        PostProfileDto postProfile = postMapper.toProfileDto(savedPost);
        postProfile.setActionType(ProfileActionType.CREATE);
        profileKafkaProducer.sendPostToKafka(postProfile);
        return postMapper.toDto(savedPost);
    }

    @Transactional
    public void deletePostById(Long id , String email) {
        Post post = postRepository.findById(id).orElseThrow();
        UserCache currentUser = userCacheRepository.findByEmail(email).orElseThrow();
        boolean isOwner = post.getUserId().equals(currentUser.getId());
        boolean isCommunityAdmin = false;
        if (post.getCommunityId() != null) {
            try{
                isCommunityAdmin = communityClient
                        .checkPermission(post.getCommunityId(),currentUser.getId() , "DELETE");
            } catch (Exception e){
                isCommunityAdmin = false;
            }
        }
        if (!isOwner && !isCommunityAdmin) {
            throw new RuntimeException("Вы не имеете право удалять пост");
        }
//        kafkaTemplate.send("post-events", new UserActivityEventDto(id , post.getUserId(),
//                null,ActionType.DELETE, "POST"));
        PostProfileDto profileDto = new PostProfileDto();
        profileDto.setId(id);
        profileDto.setUserId(post.getUserId());
        profileDto.setActionType(ProfileActionType.DELETE);
        profileKafkaProducer.sendPostToKafka(profileDto);
        postRepository.deleteById(id);
    }

    @Transactional
    public PostDto updatePost(Long id, PostDto postDto, MultipartFile file1, MultipartFile file2, MultipartFile file3, String email) {
        Post post = postRepository.findById(id).orElseThrow();
        UserCache currentUser = userCacheRepository.findByEmail(email).orElseThrow();
        boolean isOwner = post.getUserId().equals(currentUser.getId());
        boolean isCommunityAdmin = false;
        if (post.getCommunityId() != null) {
            try {
                isCommunityAdmin = communityClient
                        .checkPermission(post.getCommunityId(),currentUser.getId() , "EDIT");
            }catch (Exception e){
                isCommunityAdmin = false;
            }
        }
        if (!isOwner && !isCommunityAdmin) {
            throw new RuntimeException("У вас нету права редактировать пост");
        }
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

        PostProfileDto profileDto = postMapper.toProfileDto(updatedPost);
        profileDto.setActionType(ProfileActionType.UPDATE);
        profileKafkaProducer.sendPostToKafka(profileDto);
        return postMapper.toDto(updatedPost);
    }
}
