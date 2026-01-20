package com.example.user_service;

import com.example.user_service.image.Image;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_id")
    private Long id;
    private String email;
    private String password;
    private String name;
    private String secondName;
    private String confirmationCode;
    private Boolean enable = false;
    private String pendingEmail;
    private String emailChangeCode;
    private String passwordResetCode;
    private LocalDate birthDay;
    private Long avatarId;
    private LocalDateTime passwordResetCodeExpiryDate;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "users")
    private List<Image> photos  = new ArrayList<>();

    @ElementCollection(targetClass = Role.class , fetch = FetchType.LAZY)
    @CollectionTable(name = "users_role" , joinColumns = @JoinColumn(name = "users_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    public Users(Long id,
                 String email,
                 String password,
                 String name,
                 String secondName,
                 LocalDate birthDay) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.secondName = secondName;
        this.birthDay = birthDay;
    }

}
