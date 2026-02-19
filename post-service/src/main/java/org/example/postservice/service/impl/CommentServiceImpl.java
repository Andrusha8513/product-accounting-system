package org.example.postservice.service.impl;

import jakarta.transaction.Transactional;
import org.example.postservice.CommunityClient;
import org.example.postservice.Model.Comment;
import org.example.postservice.Model.Post;
import org.example.postservice.Model.UserCache;
import org.example.postservice.dto.ActionType;
import org.example.postservice.dto.CommentDto;
//import org.example.postservice.dto.UserActivityEventDto;
import org.example.postservice.mapper.CommentMapper;
import org.example.postservice.repository.CommentRepository;
import org.example.postservice.repository.PostRepository;
import org.example.postservice.repository.UserCacheRepository;
import org.example.postservice.service.CommentService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    private final UserCacheRepository userCacheRepository;
    private final CommunityClient communityClient;
    private final KafkaTemplate<String , Object> kafkaTemplate;
    public CommentServiceImpl(CommentRepository commentRepository,
                              PostRepository postRepository,
                              UserCacheRepository userCacheRepository, CommentMapper commentMapper ,
                              CommunityClient communityClient ,  KafkaTemplate<String , Object> kafkaTemplate) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userCacheRepository = userCacheRepository;
        this.commentMapper = commentMapper;
        this.communityClient = communityClient;
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
        if (post.getCommunityId() != null ){
            try {
                boolean canComment = communityClient
                        .checkPermission(post.getCommunityId() , userCache.getId(), "COMMENT");
                if (!canComment) {
                    throw new RuntimeException("Вы не можете писать комментарии");
                }
            }catch (Exception ex){
                throw new RuntimeException(ex);
            }
        }

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
        Comment comment = commentRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Комментарий не найден"));
        UserCache userCache = userCacheRepository.findByEmail(email).orElseThrow();
        Post post = comment.getPost();
        boolean isAuthor = comment.getUserID().equals(userCache.getId());
        boolean isCommunityModerator = comment.getUserID().equals(userCache.getId());
        if (post.getCommunityId() != null ){
            try{
                isCommunityModerator = communityClient
                        .checkPermission(post.getCommunityId() , userCache.getId(), "COMMENT");
            }catch (Exception ex){
                isCommunityModerator = false;
            }
        }
        if (!isAuthor && !isCommunityModerator) {
            throw new RuntimeException("не имеете права");
        }
//        kafkaTemplate.send("post-events", new UserActivityEventDto(id, comment.getPost().getId(),
//                comment.getUserID(), ActionType.DELETE, "COMMENT"));
        commentRepository.deleteById(id);
    }

    @Transactional
    public CommentDto updateComment(Long id, String text, String email) {
        Comment comment = commentRepository.findById(id).orElseThrow();
        UserCache userCache = userCacheRepository.findByEmail(email).orElseThrow();
        if (!comment.getUserID().equals(userCache.getId())){
           throw new RuntimeException("Вы не можете редактировать чужой комментарий");
        }
        comment.setText(text);
        Comment updatedComment = commentRepository.save(comment);
//        kafkaTemplate.send("post-events" , new UserActivityEventDto(updatedComment.getId(),
//                updatedComment.getPost().getId(), updatedComment.getUserID(),
//                ActionType.UPDATE, "COMMENT"));
        return commentMapper.toDto(comment);
    }
}
