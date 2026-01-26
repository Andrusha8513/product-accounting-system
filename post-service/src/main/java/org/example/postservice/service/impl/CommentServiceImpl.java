package org.example.postservice.service.impl;

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
    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository, UserClient userClient, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userClient = userClient;
        this.commentMapper = commentMapper;
    }

    public List<CommentDto> getAllComments(Long id,PostDto postDto) {
        List<Comment> comments = commentRepository.findAllByPost_Id(id);
        return comments.stream().map(commentMapper::toDto).toList();
    }


    public CommentDto addComment(Long postId, String text, String email) {
        Post post = postRepository.findById(postId).orElseThrow();
        UserDto userDto = userClient.getUserByEmail(email);
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setText(text);
        comment.setUserID(userDto.getId());
        commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }


    public void deleteCommentById(Long id) {
        commentRepository.deleteById(id);
    }


    public CommentDto updateComment(Long id, String text, String email) {
        Comment comment = commentRepository.findById(id).orElseThrow();
        comment.setText(text);
        return commentMapper.toDto(commentRepository.save(comment));
    }
    
}
