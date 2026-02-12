package org.example.postservice.service.impl;


import jakarta.transaction.Transactional;
import org.example.postservice.CommunityClient;
import org.example.postservice.Model.Comment;
import org.example.postservice.Model.Post;
import org.example.postservice.Model.SubComment;
import org.example.postservice.Model.UserCache;
import org.example.postservice.dto.SubCommentDto;
import org.example.postservice.mapper.SubCommentMapper;
import org.example.postservice.repository.CommentRepository;
import org.example.postservice.repository.SubCommentRepository;
import org.example.postservice.repository.UserCacheRepository;
import org.example.postservice.service.SubCommentsService;
import org.springframework.stereotype.Service;

@Service
public class SubCommentsServiceImpl implements SubCommentsService {
    private final CommentRepository commentRepository;
    private final SubCommentMapper subCommentMapper;
    private final SubCommentRepository subCommentRepository;
    private final UserCacheRepository userCacheRepository;
    private final CommunityClient communityClient;

    public SubCommentsServiceImpl(CommentRepository commentRepository ,
                                  SubCommentMapper subCommentMapper,
                                  SubCommentRepository subCommentRepository ,
                                  UserCacheRepository userCacheRepository , CommunityClient communityClient) {
        this.commentRepository = commentRepository;
        this.subCommentMapper = subCommentMapper;
        this.subCommentRepository = subCommentRepository;
        this.userCacheRepository = userCacheRepository;
        this.communityClient = communityClient;
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
        return subCommentMapper.toDto(subCommentRepository.save(subComment));
    }

    @Transactional
    public void deleteSubComment(Long id , String email) {
        Comment comment = commentRepository.findById(id).orElseThrow();
        UserCache userCache = userCacheRepository.findByEmail(email).orElseThrow();
        Post post = comment.getPost();
        boolean isAuthor = comment.getUserID().equals(userCache.getId());
        boolean isModerator = comment.getUserID().equals(userCache.getId());
        if (post.getCommunityId() != null){
            try{
                isModerator = communityClient.checkPermission(post.getCommunityId() ,
                        userCache.getId(), "COMMENT");
            }catch (Exception e){
                isModerator = false;
            }
        }
        if (!isAuthor && !isModerator){
            throw new RuntimeException("Вы не можете удалить ответ на комментарий");
        }
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
        return subCommentMapper.toDto(subCommentRepository.save(subComment));
    }
}
