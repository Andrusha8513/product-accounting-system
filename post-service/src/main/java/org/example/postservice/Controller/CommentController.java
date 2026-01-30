package org.example.postservice.Controller;

import org.example.postservice.dto.CommentDto;
import org.example.postservice.dto.PostDto;
import org.example.postservice.service.CommentService;
import org.example.postservice.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/post/comment")
public class CommentController {
    private final CommentService commentService;
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }
    @GetMapping("/{id}")
    public ResponseEntity<List<CommentDto>> getAllComments(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getAllComments(id));
    }
    @PostMapping("/create/{postId}")
    public ResponseEntity<?> addComment(@PathVariable Long postId , @RequestParam String text , Principal principal){
        CommentDto createdComment = commentService.addComment(postId,text,principal.getName());
        return ResponseEntity.ok(createdComment);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCommentById(@PathVariable Long id){
        commentService.deleteCommentById(id);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateComment(@PathVariable Long id , @RequestParam String text , Principal principal){
        CommentDto updatedComment = commentService.updateComment(id, text,principal.getName());
        return ResponseEntity.ok(updatedComment);
    }
}
