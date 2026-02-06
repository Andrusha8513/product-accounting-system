package org.example.postservice.service.impl;

import jakarta.transaction.Transactional;
import org.example.postservice.CommunityClient;
import org.example.postservice.Model.Comment;
import org.example.postservice.Model.Post;
import org.example.postservice.UserClient;
import org.example.postservice.dto.CommentDto;
import org.example.postservice.dto.PostDto;
import org.example.postservice.dto.UserDto;
import org.example.postservice.mapper.CommentMapper;
import org.example.postservice.repository.CommentRepository;
import org.example.postservice.repository.PostRepository;
import org.example.postservice.service.CommentService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    private final UserClient userClient;
    private final CommunityClient communityClient;
    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository, UserClient userClient, CommentMapper commentMapper , CommunityClient communityClient) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userClient = userClient;
        this.commentMapper = commentMapper;
        this.communityClient = communityClient;
    }

    public List<CommentDto> getAllComments(Long id) {
        List<Comment> comments = commentRepository.findAllByPost_Id(id);
        return comments.stream().map(commentMapper::toDto).toList();
    }

    @Transactional
    public CommentDto addComment(Long postId, String text, String email) {
        Post post = postRepository.findById(postId).orElseThrow();
        UserDto userDto = userClient.getUserByEmail(email);
        if (post.getCommunityId() != null ){
            try {
                boolean canComment = communityClient
                        .checkPermission(post.getCommunityId() , userDto.getId(), "COMMENT");
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
        comment.setUserID(userDto.getId());
        commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    @Transactional
    public void deleteCommentById(Long id , String email) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Комментарий не найден"));
        UserDto userDto = userClient.getUserByEmail(email);
        Post post = comment.getPost();
        boolean isAuthor = comment.getUserID().equals(userDto.getId());
        boolean isCommunityModerator = comment.getUserID().equals(userDto.getId());
        if (post.getCommunityId() != null ){
            try{
                isCommunityModerator = communityClient
                        .checkPermission(post.getCommunityId() , userDto.getId(), "COMMENT");
            }catch (Exception ex){
                isCommunityModerator = false;
            }
        }
        if (!isAuthor && !isCommunityModerator) {
            throw new RuntimeException("не имеете права");
        }
        commentRepository.deleteById(id);
    }

    @Transactional
    public CommentDto updateComment(Long id, String text, String email) {
        Comment comment = commentRepository.findById(id).orElseThrow();
        UserDto userDto = userClient.getUserByEmail(email);
        if (!comment.getUserID().equals(userDto.getId())){
           throw new RuntimeException("Вы не можете редактировать чужой комментарий");
        }
        comment.setText(text);
        return commentMapper.toDto(commentRepository.save(comment));
    }
    
}
