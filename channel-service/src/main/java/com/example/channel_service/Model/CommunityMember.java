package com.example.channel_service.Model;


import com.example.channel_service.RolesMember.CommunityRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "communitymembers")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommunityMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long communityId;
    private Long userId;
    @Enumerated(EnumType.STRING)
    private CommunityRole role;
}