package com.example.profile_service.mapper;

import com.example.profile_service.dto.ProfileSummaryDto;
import com.example.profile_service.entity.Profile;
import org.springframework.stereotype.Component;

@Component
public class SummeryMapper {
    public ProfileSummaryDto toDto(Profile profile){
        ProfileSummaryDto summary = new ProfileSummaryDto();
        summary.setName(profile.getName());
        summary.setSecondName(profile.getSecondName());
        summary.setEmail(profile.getEmail());
        summary.setBirthday(profile.getBirthday());
        return summary;
    }
}
