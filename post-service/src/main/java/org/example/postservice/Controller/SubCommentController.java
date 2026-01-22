package org.example.postservice.Controller;

import org.example.postservice.dto.SubCommentDto;
import org.example.postservice.service.CommentService;
import org.example.postservice.service.SubCommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/subcomment")
public class SubCommentController {
    private final SubCommentsService subCommentsService;
    @Autowired
    public SubCommentController(SubCommentsService subCommentsService, CommentService commentService) {
        this.subCommentsService = subCommentsService;
    }
    @PostMapping("/add/{commentId}")
    public ResponseEntity<?> addSubComment(@PathVariable Long commentId, @RequestParam String text, Principal principal) {
        return ResponseEntity.ok(subCommentsService.addSubComment(commentId, text, principal.getName()));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteSubComment(@PathVariable Long id) {
        subCommentsService.deleteSubComment(id);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateSubComment(@PathVariable Long id, @RequestParam String text) {
        return ResponseEntity.ok().body(subCommentsService.updateSubComment(id, text));
    }
}
