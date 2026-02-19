//package com.example.api_gateway;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//
//@Configuration
//@EnableWebFluxSecurity
//public class GatewaySecurityConfig {
//
//    @Bean
//    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
//        return http
//                .csrf(ServerHttpSecurity.CsrfSpec::disable)
//                .authorizeExchange(exchanges -> exchanges
//                        .pathMatchers(
//                                "/users/api/users/registration",
//                                "/users/api/users/confirm-registration",
//                                "/users/api/users/resend-confirm-registration",
//                                "/users/api/users/send-password-resetCode",
//                                "/users/api/users/reset-password-with-code",
//                                "/users/auth/**"
//                        ).permitAll()
//                        .anyExchange().authenticated()
//                )
//                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
//                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
//                .build();
//    }
//}