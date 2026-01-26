package org.example.postservice.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "images")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String originalFileName;
    private Long size;
    private String contentType;
    private boolean isPreviewImages;
    @Lob
    @Column(name = "bytes", columnDefinition = "bytea") // Для PostgreSQL нужно именно bytea
    private byte[] bytes;
    @ManyToOne(cascade = CascadeType.REFRESH , fetch = FetchType.EAGER)
    private Post post;
}
