package org.example.postservice.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "userCache")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCache {
    @Id
    private Long id;
    @Column(unique = true)

    private Long userId;
    private String email;
    private String secondName;
}
