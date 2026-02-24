package org.example.postservice.service.impl;


import jakarta.transaction.Transactional;
import org.example.postservice.Model.Comment;
import org.example.postservice.Model.Post;
import org.example.postservice.Model.SubComment;
import org.example.postservice.Model.UserCache;
import org.example.postservice.dto.ActionType;
import org.example.postservice.dto.CommunityPostEventDto;
import org.example.postservice.dto.CommunityRole;
import org.example.postservice.dto.SubCommentDto;
//import org.example.postservice.dto.UserActivityEventDto;
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
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public SubCommentsServiceImpl(CommentRepository commentRepository ,
                                  SubCommentMapper subCommentMapper,
                                  SubCommentRepository subCommentRepository ,
                                  UserCacheRepository userCacheRepository ,
                                  KafkaTemplate<String, Object> kafkaTemplate) {
        this.commentRepository = commentRepository;
        this.subCommentMapper = subCommentMapper;
        this.subCommentRepository = subCommentRepository;
        this.userCacheRepository = userCacheRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public SubCommentDto addSubComment(Long commentId, String text, String email) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        UserCache userCache = userCacheRepository.findByEmail(email).orElseThrow();
        Post post = comment.getPost();
        SubComment subComment = new SubComment();
        subComment.setComment(comment);
        subComment.setUserId(userCache.getId());
        subComment.setText(text);
        SubComment saveSubComment = subCommentRepository.save(subComment);
//        kafkaTemplate.send("post-events", new UserActivityEventDto(subComment.getId(),
//                subComment.getUserId(),comment.getId() ,ActionType.CREATE , "SUBCOMMENT"));
        return subCommentMapper.toDto(saveSubComment);
    }

    @Transactional
    public void deleteSubComment(Long id, String email) {
//        kafkaTemplate.send("post-events", new UserActivityEventDto(id, subComment.getUserId(),
//                subComment.getComment().getId(),
//                ActionType.DELETE, "SUBCOMMENT"));
        subCommentRepository.deleteById(id);
    }

    @Transactional
    public SubCommentDto updateSubComment(Long id, String text , String email) {
        SubComment subComment = subCommentRepository.findById(id).orElseThrow();
        subComment.setText(text);
        SubComment updateSubComment = subCommentRepository.save(subComment);
//        kafkaTemplate.send("post-events", new UserActivityEventDto(subComment.getId(),
//                subComment.getUserId(),subComment.getComment().getId() ,ActionType.UPDATE , "SUBCOMMENT"));
        return subCommentMapper.toDto(updateSubComment);
    }

    /// Ответы на комментарии по кафке
    @Transactional
    public void createSubCommentFromKafka(CommunityPostEventDto eventDto) {
        Comment comment = commentRepository.findById(eventDto.getParentId()).orElseThrow();
        SubComment subComment = new SubComment();
        subComment.setComment(comment);
        subComment.setUserId(eventDto.getUserId());
        subComment.setText(eventDto.getDescription());
        subCommentRepository.save(subComment);
    }
    @Transactional
    public void updateSubCommentFromKafka(CommunityPostEventDto eventDto) {
        SubComment subComment = subCommentRepository.findById(eventDto.getReplyId()).orElseThrow();
        if (subComment.getUserId().equals(eventDto.getUserId())) {
            subComment.setText(eventDto.getDescription());
            subCommentRepository.save(subComment);
        }
    }

    @Transactional
    public void deleteSubCommentFromKafka(CommunityPostEventDto eventDto) {
        SubComment subComment = subCommentRepository.findById(eventDto.getReplyId()).orElseThrow();
        boolean isAuthor = subComment.getUserId().equals(eventDto.getUserId());
        boolean isPrivileged = CommunityRole.ADMIN.name().equalsIgnoreCase(eventDto.getActorRole()) || CommunityRole.EDITOR.name().equalsIgnoreCase(eventDto.getActorRole());
        try {
            if (isAuthor || isPrivileged) {
                subCommentRepository.deleteById(subComment.getId());
            }
        } catch (Exception e) {
            throw new RuntimeException("нет доступа");
        }
    }
}
