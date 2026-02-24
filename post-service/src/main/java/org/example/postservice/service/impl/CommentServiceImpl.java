package org.example.postservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.postservice.Model.Comment;
import org.example.postservice.Model.Post;
import org.example.postservice.Model.UserCache;
import org.example.postservice.dto.ActionType;
import org.example.postservice.dto.CommentDto;
//import org.example.postservice.dto.UserActivityEventDto;
import org.example.postservice.dto.CommunityPostEventDto;
import org.example.postservice.dto.CommunityRole;
import org.example.postservice.mapper.CommentMapper;
import org.example.postservice.repository.CommentRepository;
import org.example.postservice.repository.PostRepository;
import org.example.postservice.repository.UserCacheRepository;
import org.example.postservice.service.CommentService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    private final UserCacheRepository userCacheRepository;
    private final KafkaTemplate<String , Object> kafkaTemplate;
    public CommentServiceImpl(CommentRepository commentRepository,
                              PostRepository postRepository,
                              UserCacheRepository userCacheRepository, CommentMapper commentMapper ,
                               KafkaTemplate<String , Object> kafkaTemplate) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userCacheRepository = userCacheRepository;
        this.commentMapper = commentMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    public List<CommentDto> getAllComments(Long id) {
        List<Comment> comments = commentRepository.findAllByPost_Id(id);
        return comments.stream().map(commentMapper::toDto).toList();
    }

    @Transactional
    public CommentDto addComment(Long postId, String text, String email) {
        Post post = postRepository.findById(postId).orElseThrow();
        UserCache userCache = userCacheRepository.findByEmail(email).orElseThrow();
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setText(text);
        comment.setUserID(userCache.getId());
        comment =  commentRepository.save(comment);
//        kafkaTemplate.send("post-events", new UserActivityEventDto(comment.getId(), comment.getUserID(),
//                post.getId(), ActionType.CREATE, "COMMENT"));
        return commentMapper.toDto(comment);
    }

    @Transactional
    public void deleteCommentById(Long id , String email) {
        Comment comment = commentRepository.findById(id).orElseThrow();
//        kafkaTemplate.send("post-events", new UserActivityEventDto(id, comment.getPost().getId(),
//                comment.getUserID(), ActionType.DELETE, "COMMENT"));
        commentRepository.deleteById(id);
    }

    @Transactional
    public CommentDto updateComment(Long id, String text, String email) {
        Comment comment = commentRepository.findById(id).orElseThrow();
        UserCache userCache = userCacheRepository.findByEmail(email).orElseThrow();
        comment.setText(text);
        Comment updatedComment = commentRepository.save(comment);
//        kafkaTemplate.send("post-events" , new UserActivityEventDto(updatedComment.getId(),
//                updatedComment.getPost().getId(), updatedComment.getUserID(),
//                ActionType.UPDATE, "COMMENT"));
        return commentMapper.toDto(comment);
    }

    /// Кафка для комментов
    @Transactional
    public void createCommentFromKafka(CommunityPostEventDto eventDto){
        Post post = postRepository.findById(eventDto.getPostId()).orElseThrow();
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUserID(eventDto.getUserId());
        comment.setText(eventDto.getDescription());
        commentRepository.save(comment);
    }
    @Transactional
    public void updateCommentFromKafka(CommunityPostEventDto eventDto) {
        Comment comment = commentRepository.findById(eventDto.getCommentId()).orElseThrow();
        boolean isAuthor = comment.getUserID().equals(eventDto.getUserId());
        if (!isAuthor) {
            log.error("ошибка доступа");
            return;
        }
        comment.setText(eventDto.getDescription());
        commentRepository.save(comment);
    }
    @Transactional
    public void deleteCommentFromKafka(CommunityPostEventDto eventDto) {
        Comment comment = commentRepository.findById(eventDto.getCommentId()).orElseThrow();
        boolean isAuthor = comment.getUserID().equals(eventDto.getUserId());
        boolean isPrivilege = CommunityRole.ADMIN.name().equals(eventDto.getActorRole()) || CommunityRole.EDITOR.name().equals(eventDto.getActorRole());
        try {
            if (isAuthor ||  isPrivilege) {
                commentRepository.deleteById(comment.getId());
            }
        } catch (Exception e) {
            throw new RuntimeException("нет доступа");
        }
    }
}
