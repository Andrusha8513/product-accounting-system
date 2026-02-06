package com.example.channel_service.Config;

import com.example.channel_service.Dto.PostDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "posts", configuration = FeignMultipartConfig.class)
public interface PostClient {

    @PostMapping(value = "/api/post/createpost", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    PostDto createPost(@ModelAttribute PostDto postDto,
                       @RequestPart(value = "file1", required = false) MultipartFile file1,
                       @RequestPart(value = "file2", required = false) MultipartFile file2,
                       @RequestPart(value = "file3", required = false) MultipartFile file3,
                       @RequestParam("email") String email);

    @PostMapping(value = "/api/post/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    PostDto updatePost(@PathVariable("id") Long id,
                       @ModelAttribute PostDto postDto,
                       @RequestPart(value = "file1", required = false) MultipartFile file1,
                       @RequestPart(value = "file2", required = false) MultipartFile file2,
                       @RequestPart(value = "file3", required = false) MultipartFile file3,
                       @RequestParam("email") String email);

    @DeleteMapping("/api/post/{id}")
    void deletePost(@PathVariable Long id , String email);
}
