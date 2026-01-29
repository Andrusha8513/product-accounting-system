package com.example.profile_service;

import com.example.profile_service.dto.FullProfileDto;
import com.example.profile_service.feignClient.PostClient;
import com.example.profile_service.feignClient.UsersProfile;
import com.example.user_service.dto.PrivetUserProfileDto;
import lombok.RequiredArgsConstructor;
import org.example.postservice.dto.PostDto;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {
   private  final UsersProfile usersProfile;
   private  final PostClient postClient;

public FullProfileDto getMyProfile(String email){
   PrivetUserProfileDto myInfo = usersProfile.getMyProfile(email);

    List<PostDto> myPosts = postClient.findAllPostsByUserId(myInfo.getId());

    return FullProfileDto.builder()
            .userDetails(myInfo)
            .posts(myPosts != null ? myPosts : Collections.emptyList())
            .build();

}
}
