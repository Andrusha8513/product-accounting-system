package com.example.profile_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "images_post")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImagePost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String originalFileName;
    private Long size;
    private String contentType;
    private boolean isPreviewImages;
    @Lob
    @Column(name = "bytes")
    private byte[] bytes;


    @ManyToOne(cascade = CascadeType.REFRESH , fetch = FetchType.EAGER)
    @JsonIgnore
    private PostProfile post;
}
