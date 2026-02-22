package com.example.profile_service.entity;

import com.example.profile_service.image.Image;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "profile")
@NoArgsConstructor
public class Profile {
    @Id
    @Column(name = "profile_id")
    private Long id;

    private String name;
    private String secondName;
    private String email;
    private String password;
    private LocalDate birthday;
    private Long avatarId;


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "profile")
    private List<Image> photos  = new ArrayList<>();


    @OneToMany(cascade = CascadeType.ALL , fetch = FetchType.EAGER ,orphanRemoval = true, mappedBy = "profile")
    private List<PostProfile> postProfiles = new ArrayList<>();
}
