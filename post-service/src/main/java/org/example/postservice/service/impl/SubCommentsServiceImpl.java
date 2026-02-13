package org.example.postservice.service.impl;


import jakarta.transaction.Transactional;
import org.example.postservice.CommunityClient;
import org.example.postservice.Model.Comment;
import org.example.postservice.Model.Post;
import org.example.postservice.Model.SubComment;
import org.example.postservice.Model.UserCache;
import org.example.postservice.dto.ActionType;
import org.example.postservice.dto.SubCommentDto;
import org.example.postservice.dto.UserActivityEventDto;
import org.example.postservice.mapper.SubCommentMapper;
import org.example.postservice.repository.CommentRepository;
import org.example.postservice.repository.SubCommentRepository;
import org.example.postservice.repository.UserCacheRepository;
import org.example.postservice.service.SubCommentsService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class SubCommentsServiceImpl implements SubCommentsService {
    private final CommentRepository commentRepository;
    private final SubCommentMapper subCommentMapper;
    private final SubCommentRepository subCommentRepository;
    private final UserCacheRepository userCacheRepository;
    private final CommunityClient communityClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public SubCommentsServiceImpl(CommentRepository commentRepository ,
                                  SubCommentMapper subCommentMapper,
                                  SubCommentRepository subCommentRepository ,
                                  UserCacheRepository userCacheRepository , CommunityClient communityClient,
                                  KafkaTemplate<String, Object> kafkaTemplate) {
        this.commentRepository = commentRepository;
        this.subCommentMapper = subCommentMapper;
        this.subCommentRepository = subCommentRepository;
        this.userCacheRepository = userCacheRepository;
        this.communityClient = communityClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public SubCommentDto addSubComment(Long commentId, String text, String email) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        UserCache userCache = userCacheRepository.findByEmail(email).orElseThrow();
        Post post = comment.getPost();
        if (post.getCommunityId() != null){
            try{
                boolean addSubComment = communityClient
                        .checkPermission(post.getCommunityId() , userCache.getId(), "COMMENT");
                if (!addSubComment){
                    throw new RuntimeException("Вы не можете оставлять ответы на комментарии");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        SubComment subComment = new SubComment();
        subComment.setComment(comment);
        subComment.setUserId(userCache.getId());
        subComment.setText(text);
        SubComment saveSubComment = subCommentRepository.save(subComment);
        kafkaTemplate.send("post-events", new UserActivityEventDto(subComment.getId(),
                subComment.getUserId(),comment.getId() ,ActionType.CREATE , "SUBCOMMENT"));
        return subCommentMapper.toDto(saveSubComment);
    }

    @Transactional
    public void deleteSubComment(Long id, String email) {
        SubComment subComment = subCommentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ответ не найден"));
        UserCache userCache = userCacheRepository.findByEmail(email).orElseThrow();
        boolean isAuthor = subComment.getUserId().equals(userCache.getId());
        Post post = subComment.getComment().getPost();
        boolean isModerator = false;
        if (post.getCommunityId() != null) {
            try {
                isModerator = communityClient.checkPermission(post.getCommunityId(), userCache.getId(), "COMMENT");
            } catch (Exception e) { isModerator = false; }
        }
        if (!isAuthor && !isModerator) {
            throw new RuntimeException("Вы не можете удалить ответ на комментарий");
        }
        kafkaTemplate.send("post-events", new UserActivityEventDto(id, subComment.getUserId(),
                subComment.getComment().getId(),
                ActionType.DELETE, "SUBCOMMENT"));
        subCommentRepository.deleteById(id);
    }

    @Transactional
    public SubCommentDto updateSubComment(Long id, String text , String email) {
        SubComment subComment = subCommentRepository.findById(id).orElseThrow();
        UserCache userCache = userCacheRepository.findByEmail(email).orElseThrow();
        if (!subComment.getUserId().equals(userCache.getId())){
            throw new RuntimeException("Вы не можете редактировать чужой ответ");
        }
        subComment.setText(text);
        SubComment updateSubComment = subCommentRepository.save(subComment);
        kafkaTemplate.send("post-events", new UserActivityEventDto(subComment.getId(),
                subComment.getUserId(),subComment.getComment().getId() ,ActionType.UPDATE , "SUBCOMMENT"));
        return subCommentMapper.toDto(updateSubComment);
    }
}
