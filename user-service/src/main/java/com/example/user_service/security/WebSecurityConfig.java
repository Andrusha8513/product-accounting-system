package com.example.user_service.security;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.session.SessionRegistry;
//import org.springframework.security.core.session.SessionRegistryImpl;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.provisioning.JdbcUserDetailsManager;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
//import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
//import org.springframework.security.web.session.HttpSessionEventPublisher;
//
//import javax.sql.DataSource;
//
//@Configuration
//@EnableWebSecurity
//public class WebSecurityConfig {
//    @Autowired
//    private DataSource dataSource;
//
//    @Bean
//    public SessionRegistry sessionRegistry(){
//        return new SessionRegistryImpl();
//    }
//

//    @Bean
//    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventListener(){
//        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
//    }
//
//    @Bean
//    public PersistentTokenRepository persistentTokenRepository(){
//        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
//        tokenRepository.setDataSource(dataSource);
//        return tokenRepository;
//    }
//
//    @Bean
//    public JdbcUserDetailsManager userDetailsManager(){
//        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
//        manager.setUsersByUsernameQuery(
//                "select email, password , enable from users where email=?");
//        manager.setAuthoritiesByUsernameQuery(
//                "select u.email, ur.roles from users u inner join users_role ur on u.users_id = ur.users_id where u.email=?");
//        return manager;
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
//        return configuration.getAuthenticationManager();
//    }
//
//    @Bean
//    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
//        http.sessionManagement(session -> session
//                .maximumSessions(5)
//                .maxSessionsPreventsLogin(false)
//                .sessionRegistry(sessionRegistry()))
//
//                .csrf(csrf -> csrf.disable())
//
//                .authorizeHttpRequests(request ->  request
//                        .anyRequest().permitAll())
//                .formLogin(form -> form
//                        .loginProcessingUrl("/login")
//                        .usernameParameter("email")
//                        .passwordParameter("password")
//                        .permitAll())
//
//                .rememberMe(remeber -> remeber
//                        .alwaysRemember(true)
//                        .tokenRepository(persistentTokenRepository())
//                        .tokenValiditySeconds(60 * 10)
//                        .userDetailsService(userDetailsManager()))
//
//                .logout(logout -> logout
//                        .deleteCookies("JSESSIONID", "remember-me")
//                        .permitAll());
//        return http.build();
//
//    }
//
//}
