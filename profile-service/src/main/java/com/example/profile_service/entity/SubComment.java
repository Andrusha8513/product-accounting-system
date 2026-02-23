package com.example.profile_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subcomments")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    private Long userId;


    @ManyToOne
    private Comment comment;
}
