package org.example.postservice.security;


import lombok.RequiredArgsConstructor;
import org.example.postservice.security.JwT.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Доступны всем без токена
                        .requestMatchers(HttpMethod.GET, "/api/post/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/post/comment/**").permitAll()

                        //Маршруты для которых нужен токен(ПОСТЫ)
                        .requestMatchers("/api/post/createpost").authenticated()
                        .requestMatchers("/api/post/delete/**").authenticated()
                        .requestMatchers("/api/post/update/**").authenticated()

                        //Маршруты для которых нужен токен(КОММЕНТАРИИ)
                        .requestMatchers("/api/post/comment/create/**").authenticated()
                        .requestMatchers("/api/post/comment/update/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/post/comment/**").authenticated()

                        //Маршруты для которых нужен токен(ПОДКОММЕНТАРИИ)
                        .requestMatchers("/api/subcomment/**").authenticated()


                        .anyRequest().authenticated()
                )
                //Берем JWT фильтр
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}