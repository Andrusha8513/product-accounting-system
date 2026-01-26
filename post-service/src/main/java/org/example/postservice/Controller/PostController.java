package org.example.postservice.Controller;

import org.example.postservice.Model.Post;
import org.example.postservice.UserClient;
import org.example.postservice.dto.PostDto;
import org.example.postservice.repository.PostRepository;
import org.example.postservice.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/post")
public class PostController {
    private final PostService postService;
    public PostController(PostService postService) {
        this.postService = postService;
    }
    @GetMapping
    public ResponseEntity<List<PostDto>> findAllPosts() {
        List<PostDto> postDto = postService.findAllPosts();
        return ResponseEntity.ok(postDto);
    }
    @GetMapping("/api/post/{userId}")
    public ResponseEntity<List<PostDto>> findAllPostsByUserId(@PathVariable Long userId) {
        List<PostDto> postDto = postService.findAllPostsByUserId(userId);
        return ResponseEntity.ok(postDto);
    }
    @GetMapping("/{id}")
    public ResponseEntity<PostDto> findPostById(@PathVariable Long id){
        PostDto postDto = postService.findPostById(id);
        return ResponseEntity.ok(postDto);
    }
    @PostMapping("/createpost")
    public ResponseEntity<?> createPost(@ModelAttribute PostDto postDto,
                                        @RequestPart(required = false) MultipartFile file1,
                                        @RequestPart(required = false) MultipartFile file2, @RequestPart(required = false)MultipartFile file3 ,
                                        Principal principal) {
        PostDto createdpost = postService.createPost(postDto,file1,file2,file3,principal.getName());
        return ResponseEntity.ok(createdpost);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?>  deletePostById(@PathVariable Long id , Principal principal) {
        postService.deletePostById(id , principal.getName());
        return ResponseEntity.ok().build();
    }
    @PostMapping("/update/{id}")
    public ResponseEntity<?> updatePostById(@PathVariable  Long id, @ModelAttribute PostDto postDto ,
                                            @RequestPart(required = false) MultipartFile file1,
                                            @RequestPart(required = false) MultipartFile file2,
                                            @RequestPart(required = false) MultipartFile file3 , Principal principal) {
        PostDto post = postService.updatePost(id , postDto,file1,file2,file3,principal.getName());
        return ResponseEntity.ok(post);
    }
}
