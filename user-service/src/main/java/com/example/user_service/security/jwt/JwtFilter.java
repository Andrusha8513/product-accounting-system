package com.example.user_service.security.jwt;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);
        if (token != null && jwtService.validateJwtToken(token)) {
          TokenData tokenData =  jwtService.extractTokenData(token);

            List<GrantedAuthority> authorities = tokenData.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.name()))
                    .collect(Collectors.toList());

            UserDetails userDetails = User.builder()
                    .username(tokenData.getEmail())
                    .password("")
                    .authorities(authorities)
                    .accountExpired(false)
                    .accountLocked(!tokenData.getIsAccountNonLocked())
                    .credentialsExpired(false)
                    .disabled(!tokenData.getIsEnabled())
                    .build();


            Authentication auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, authorities
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }



    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }



//    private void setCustomDetailsToSecurityContextHolder(String token) {
//        String email = jwtService.getEmailFromToken(token);
//        Set<Role> roles = jwtService.getRoleFromToken(token);
//        CustomUserDetails customUserDerails = customUserService.loadUserByUsername(email);
//        List<GrantedAuthority> authorities = roles.stream()
//                .map(role -> new SimpleGrantedAuthority(role.name()))
//                .collect(Collectors.toList());
//
//        //пока для тестов отключил
////        if (!customUserDerails.isEnabled()){
////            throw new DisabledException("Аккаунт  не активирован");
////        }
//
//        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(customUserDerails, null,
//                authorities);
//        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//    }
}
