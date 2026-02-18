//package com.example.user_service.security;
//
//import com.example.user_service.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class CustomUserService implements UserDetailsService {
//private final UserRepository userRepository;
//
//    @Override
//    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return userRepository.findByEmail(username).map(CustomUserDetails::new)
//                .orElseThrow(() -> new UsernameNotFoundException(username));
//    }
//}
