package com.example.user_service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final EmailClient emailClient;

    public Users createUsers(Users users) {
        if (userRepository.findByEmail((users.getEmail())).isPresent()) {
            throw new IllegalArgumentException("Пользователь с такой почтой уже существует");
        }

        if (users.getPassword().length() < 8) {
            throw new IllegalArgumentException("Пароль должен быть длинней восьми символом");
        }
        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        users.setRoles(Set.of(Role.ROLE_USER));
        users.setConfirmationCode(code);
        Users saveUsers = userRepository.save(users);

        emailClient.senConfirmationCode(users.getEmail() , users.getConfirmationCode());
        return saveUsers;
    }


}
