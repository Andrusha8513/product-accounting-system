//package com.example.user_service.security.jwt;
//
//import com.example.user_service.Users;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//@Entity
//@NoArgsConstructor
//@AllArgsConstructor
//@Getter
//@Setter
//@Table(name = "users_session")
//public class JwtToken {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "users_session_id")
//    private Long id;
//
//    private String refreshToken;
//
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "users_id" , nullable = false)
//    private Users users;
//}
