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
        List<CommunityDto> communities = communityService.findAllCommunity();
        return ResponseEntity.ok(communities);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getCommunityById(@PathVariable Long id){
        return ResponseEntity.ok(communityService.getCommunityById(id));
    }
    @PostMapping("/create")
    private ResponseEntity<?> createCommunity(@RequestParam String name ,
                                              @RequestParam String description , Principal principal) {
        return ResponseEntity.ok(communityService.createCommunity(name , description , principal.getName()));
    }
    @PostMapping("/{communityId}/join")
    public ResponseEntity<Void> joinCommunity(@PathVariable Long communityId , Principal principal) {
        communityService.join(communityId, principal.getName());
        return ResponseEntity.ok().build();
    }
    @PostMapping("/{communityId}/unjoin")
    public ResponseEntity<Void> unJoinCommunity(@PathVariable Long communityId , Principal principal) {
        communityService.unJoin(communityId, principal.getName());
        return ResponseEntity.ok().build();
    }
    @PostMapping("/{communityId}/role")
    public ResponseEntity<Void> changeRole(@PathVariable Long communityId ,
                                           @RequestParam Long userId , @RequestParam CommunityRole role , Principal principal) {
        communityService.changeRole(communityId, userId, role, principal.getName());
        return ResponseEntity.ok().build();
    }
    @PostMapping("{communityId}/posts")
    public ResponseEntity<?> createPost(@PathVariable Long communityId , @ModelAttribute PostDto postDto,
                                        @RequestPart(required = false) MultipartFile f1 ,
                                        @RequestPart(required = false) MultipartFile f2 ,
                                        @RequestPart(required = false) MultipartFile f3,Principal principal) {
        return ResponseEntity.ok(communityService.createPostInCommunity(communityId, postDto, f1, f2, f3, principal.getName()));
    }
    @DeleteMapping("/{communityId}/posts/{postId}/delete")
    public ResponseEntity<Void> deletePostById(@PathVariable Long communityId, @PathVariable Long postId,
                                                Principal principal) {
        communityService.deletePostById(communityId, postId, principal.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{communityId}/posts/{postId}/update")
    public ResponseEntity<?> updatePost(@PathVariable Long communityId ,@PathVariable Long postId,
                                        @ModelAttribute PostDto postDto, @RequestPart(required = false) MultipartFile f1,
                                        @RequestPart(required = false) MultipartFile f2 ,
                                        @RequestPart(required = false) MultipartFile f3,
                                        Principal principal) {
        return ResponseEntity.ok(communityService.updatePostInCommunity(communityId, postId, postDto, f1, f2, f3, principal.getName()));
    }
    @GetMapping("/{id}/check-permission")
    public boolean checkPermission(@PathVariable Long id , @RequestParam Long userId ,
                                   @RequestParam String action) {
        return communityService.checkPermission(id, userId, action);
    }
}
