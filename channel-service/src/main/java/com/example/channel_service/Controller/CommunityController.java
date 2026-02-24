package com.example.channel_service.Controller;

import com.example.channel_service.Dto.CommunityDto;
import com.example.channel_service.Dto.PostDto;
import com.example.channel_service.Model.Community;
import com.example.channel_service.RolesMember.CommunityRole;
import com.example.channel_service.Service.CommunityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/community")
public class CommunityController {
    private final CommunityService communityService;
    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }
    @GetMapping
    public ResponseEntity<List<CommunityDto>> findAllCommunities(){
        return ResponseEntity.ok(communityService.findAllCommunity());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCommunityById(@PathVariable Long id){
        return ResponseEntity.ok(communityService.getCommunityById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createCommunity(@RequestParam String name,
                                             @RequestParam String description) {
        return ResponseEntity.ok(communityService.createCommunity(name, description));
    }

    @PostMapping("/{communityId}/join")
    public ResponseEntity<Void> joinCommunity(@PathVariable Long communityId) {
        communityService.join(communityId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{communityId}/unjoin")
    public ResponseEntity<Void> unJoinCommunity(@PathVariable Long communityId) {
        communityService.unJoin(communityId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{communityId}/role")
    public ResponseEntity<Void> changeRole(@PathVariable Long communityId,
                                           @RequestParam("userId") Long userId, // Явно указываем имя
                                           @RequestParam CommunityRole role) {
        communityService.changeRole(communityId, userId, role);
        return ResponseEntity.ok().build();
    }

    /// KAFKA
    @PostMapping("/{communityId}/posts")
    public ResponseEntity<String> createPost(@PathVariable Long communityId,
                                             @ModelAttribute PostDto postDto,
                                             @RequestPart(required = false) MultipartFile f1,
                                             @RequestPart(required = false) MultipartFile f2,
                                             @RequestPart(required = false) MultipartFile f3) {
        communityService.createPostInCommunity(communityId, postDto, f1, f2, f3);
        return ResponseEntity.accepted().body("Запрос на создание поста отправлен в очередь");
    }
    /// KAFKA
    @PutMapping("/{communityId}/posts/{postId}")
    public ResponseEntity<String> updatePost(@PathVariable Long communityId,
                                             @PathVariable Long postId,
                                             @ModelAttribute PostDto postDto,
                                             @RequestPart(required = false) MultipartFile f1,
                                             @RequestPart(required = false) MultipartFile f2,
                                             @RequestPart(required = false) MultipartFile f3) {
        communityService.updatePostInCommunity(postId, communityId, postDto, f1, f2, f3);
        return ResponseEntity.accepted().body("Запрос на обновление поста отправлен в очередь");
    }
    /// KAFKA
    @DeleteMapping("/{communityId}/posts/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long communityId,
                                             @PathVariable Long postId) {
        communityService.deletePostById(communityId, postId);
        return ResponseEntity.accepted().body("Запрос на удаление поста отправлен в очередь");
    }

    /// KAFKA
    @PostMapping("/{communityId}/posts/{postId}/comments")
    public ResponseEntity<String> createComment(@PathVariable Long communityId,
                                                @PathVariable Long postId,
                                                @RequestParam String text) {
        communityService.createCommentInPostOnCommunity(communityId, postId, text);
        return ResponseEntity.accepted().body("Запрос на создание комментария отправлен");
    }
    /// KAFKA
    @PutMapping("/{communityId}/posts/{postId}/comments/{commentId}")
    public ResponseEntity<String> updateComment(@PathVariable Long communityId,
                                                @PathVariable Long postId,
                                                @PathVariable Long commentId,
                                                @RequestParam String text) {
        communityService.updateCommentInPostOnCommunity(postId, communityId, commentId, text);
        return ResponseEntity.accepted().body("Запрос на изменение комментария отправлен");
    }
    /// KAFKA
    @DeleteMapping("/{communityId}/posts/{postId}/comments/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long communityId,
                                                @PathVariable Long postId,
                                                @PathVariable Long commentId) {
        communityService.deleteCommentInPostOnCommunity(postId, communityId, commentId);
        return ResponseEntity.accepted().body("Запрос на удаление комментария отправлен");
    }

    /// KAFKA
    @PostMapping("/{communityId}/posts/{postId}/comments/{commentId}/replies")
    public ResponseEntity<String> createReply(@PathVariable Long communityId,
                                              @PathVariable Long postId,
                                              @PathVariable Long commentId,
                                              @RequestParam String text) {
        communityService.createReplyToCommentInPostOnCommunity(postId, communityId, commentId, text);
        return ResponseEntity.accepted().body("Запрос на ответ отправлен");
    }
    /// KAFKA
    @PutMapping("/{communityId}/posts/{postId}/comments/{commentId}/replies/{replyId}")
    public ResponseEntity<String> updateReply(@PathVariable Long communityId,
                                              @PathVariable Long postId,
                                              @PathVariable Long commentId,
                                              @PathVariable Long replyId,
                                              @RequestParam String text) {
        communityService.updateReplyToCommentInPostOnCommunity(postId, communityId, commentId, replyId, text);
        return ResponseEntity.accepted().body("Запрос на изменение ответа отправлен");
    }
    /// KAFKA
    @DeleteMapping("/{communityId}/posts/{postId}/comments/{commentId}/replies/{replyId}")
    public ResponseEntity<String> deleteReply(@PathVariable Long communityId,
                                              @PathVariable Long postId,
                                              @PathVariable Long commentId,
                                              @PathVariable Long replyId) {
        communityService.deleteReplyToCommentInPostOnCommunity(postId, communityId, commentId, replyId);
        return ResponseEntity.accepted().body("Запрос на удаление ответа отправлен");
    }
}
