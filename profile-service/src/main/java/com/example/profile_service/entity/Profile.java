package com.example.profile_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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
    private LocalDate birthday;
    private Long avatarId;

    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "profile")
    private List<Image> photos  = new ArrayList<>();

    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(cascade = CascadeType.ALL , fetch = FetchType.LAZY ,orphanRemoval = true, mappedBy = "profile")
    private List<PostProfile> postProfiles = new ArrayList<>();
}
