package com.example.user_service;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Long id;

    private String email;
    private String password;
    private String name;
    private String secondName;
    private String endName;
    private String confirmationCode;

    public Users(Long id,String email,String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    @ElementCollection(targetClass = Role.class , fetch = FetchType.LAZY)
    @CollectionTable(name = "users_role" , joinColumns = @JoinColumn(name = "users_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;
}
