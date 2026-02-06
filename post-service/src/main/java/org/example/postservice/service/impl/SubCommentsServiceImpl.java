package org.example.postservice.service.impl;


import jakarta.transaction.Transactional;
import org.example.postservice.CommunityClient;
import org.example.postservice.Model.Comment;
import org.example.postservice.Model.Post;
import org.example.postservice.Model.SubComment;
import org.example.postservice.UserClient;
import org.example.postservice.dto.SubCommentDto;
import org.example.postservice.dto.UserDto;
import org.example.postservice.mapper.CommentMapper;
import org.example.postservice.mapper.SubCommentMapper;
import org.example.postservice.repository.CommentRepository;
import org.example.postservice.repository.SubCommentRepository;
import org.example.postservice.service.SubCommentsService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SubCommentsServiceImpl implements SubCommentsService {
    private final CommentRepository commentRepository;
    private final SubCommentMapper subCommentMapper;
    private final SubCommentRepository subCommentRepository;
    private final UserClient userClient;
    private final CommunityClient communityClient;

    public SubCommentsServiceImpl(CommentRepository commentRepository ,
                                  SubCommentMapper subCommentMapper,
                                  SubCommentRepository subCommentRepository ,
                                  UserClient userClient , CommunityClient communityClient) {
        this.commentRepository = commentRepository;
        this.subCommentMapper = subCommentMapper;
        this.subCommentRepository = subCommentRepository;
        this.userClient = userClient;
        this.communityClient = communityClient;
    }

    @Transactional
    public SubCommentDto addSubComment(Long commentId, String text, String email) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        UserDto userDto = userClient.getUserByEmail(email);
        Post post = comment.getPost();
        if (post.getCommunityId() != null){
            try{
                boolean addSubComment = communityClient
                        .checkPermission(post.getCommunityId() , userDto.getId(), "COMMENT");
                if (!addSubComment){
                    throw new RuntimeException("Вы не можете оставлять ответы на комментарии");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        SubComment subComment = new SubComment();
        subComment.setComment(comment);
        subComment.setUserId(userDto.getId());
        subComment.setText(text);
        return subCommentMapper.toDto(subCommentRepository.save(subComment));
    }

    @Transactional
    public void deleteSubComment(Long id , String email) {
        Comment comment = commentRepository.findById(id).orElseThrow();
        UserDto userDto = userClient.getUserByEmail(email);
        Post post = comment.getPost();
        boolean isAuthor = comment.getUserID().equals(userDto.getId());
        boolean isModerator = comment.getUserID().equals(userDto.getId());
        if (post.getCommunityId() != null){
            try{
                isModerator = communityClient.checkPermission(post.getCommunityId() ,
                        userDto.getId(), "COMMENT");
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
        UserDto userDto = userClient.getUserByEmail(email);
        if (!subComment.getUserId().equals(userDto.getId())){
            throw new RuntimeException("Вы не можете редактировать чужой ответ");
        }
        subComment.setText(text);
        return subCommentMapper.toDto(subCommentRepository.save(subComment));
    }
}
