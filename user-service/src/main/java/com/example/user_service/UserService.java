package com.example.user_service;

import com.example.user_service.dto.UserDto;
import com.example.user_service.dto.UserRegistrationDTO;
import com.example.user_service.dto.mapping.UserMapper;
import com.example.user_service.dto.mapping.UserMapperNew;
import com.example.user_service.dto.mapping.UserMapping;
import com.example.user_service.image.Image;
import com.example.user_service.image.ImageRepository;
import com.example.user_service.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailClient emailClient;
    private final ImageService imageService;
    private final ImageRepository imageRepository;
    private final UserMapper userMapper;
    private final SessionRegistry sessionRegistry;
    private final UserMapperNew  userMapperNew;
    // private final UserMapping userMapping;//добавил , пока не юзал. На будущее мб пока оставлю


    public UserDto getUserBySecondName( String secondName) {
        Users user = userRepository.getUserBySecondName(secondName).orElseThrow();
        return userMapperNew.toDto(user);
    }


    public UserDto getInfoById(Long id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("паштетус лохетус"));
        return userMapperNew.toDto(user);
    }
    public UserDto getUserByEmail(String email) {
        Users user = userRepository.getUserByEmail(email).orElseThrow(() -> new RuntimeException("паштетус лохетус"));
        return userMapperNew.toDto(user);
    }

    public void createUsers(UserRegistrationDTO userDto) {
        if (userRepository.findByEmail((userDto.getEmail())).isPresent()) {
            throw new IllegalArgumentException("Пользователь с такой почтой уже существует");
        }
        if (userDto.getPassword().length() < 8) {
            throw new IllegalArgumentException("Пароль должен быть длинней восьми символом");
        }
        Users users =  userMapper.toEntity(userDto);
        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        LocalDateTime expireDate = LocalDateTime.now().plusMinutes(15);
        users.setPasswordResetCodeExpiryDate(expireDate);
        users.setPassword(passwordEncoder.encode(users.getPassword()));
        users.setConfirmationCode(code);
        userRepository.save(users);

        emailClient.sendConfirmationCode(users.getEmail(), users.getConfirmationCode());
    }

    @Transactional
    public boolean confirmRegistration(String code) {
        Optional<Users> usersOptional = userRepository.findByConfirmationCode(code);

        if (usersOptional.isPresent()) {
            Users users = usersOptional.get();
            users.setEnable(true);
            users.setConfirmationCode(null);
            userRepository.save(users);
            return true;
        }
        return false;
    }

    @Transactional
    public void resendConfirmationCode(String email) {
        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с такой почтой не найден"));

        if (users.getEnable()) {
            throw new IllegalArgumentException("Аккаунт уже активирован");
        }
        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        users.setConfirmationCode(code);
        userRepository.save(users);
        emailClient.sendConfirmationCode(email, code);
    }

    @Transactional
    public void sendPasswordResetCode(String email) {
        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с такой почтой не найден"));

        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();


        users.setPasswordResetCode(code);
        userRepository.save(users);
        emailClient.sendPasswordResetCode(email, code);
    }

    @Transactional
    public void resetPasswordWithCode(String email, String code, String newPassword) {
        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с такой почтой не найден"));

        if (users.getPasswordResetCode() == null || !users.getPasswordResetCode().equals(code)) {
            throw new IllegalArgumentException("Неверный код для сброса пароля");
        }

        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("Пароля не должен быть короче восьми символов!");
        }

        users.setPassword(passwordEncoder.encode(newPassword));
        users.setPasswordResetCode(null);
        users.setPasswordResetCodeExpiryDate(null);
        userRepository.save(users);
    }

    @Transactional
    public void sendEmailResetCode(String email, String newEmail) {

        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        if (userRepository.findByEmail(newEmail).isPresent()) {
            throw new IllegalArgumentException("Почта уже занята");
        }
        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        users.setEmailChangeCode(code);
        users.setPendingEmail(newEmail);

        userRepository.save(users);
        emailClient.sendConfirmationCode(newEmail, code);
    }

    @Transactional
    public void resendEmailResetCode(String pendingEmail) {
        Users users = userRepository.findByPendingEmail(pendingEmail)
                .orElseThrow(() -> new IllegalArgumentException("Почта не найдена"));

        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        users.setEmailChangeCode(code);
        userRepository.save(users);
        emailClient.sendConfirmationCode(pendingEmail, code);
    }

    @Transactional
    public void confirmEmailChange(Long id, String code) {
        Users users = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с таким id" + id + " не найден"));

        if (users.getEmailChangeCode() == null || !users.getEmailChangeCode().equals(code)) {
            throw new IllegalArgumentException("Неверный код подтверждения.");
        }

        users.setEmail(users.getPendingEmail());

        users.setPendingEmail(null);
        users.setEmailChangeCode(null);
        userRepository.save(users);
    }


    @Transactional
    public void updateUserPassword(Long id,
                                   String newPassword,
                                   String currenPassword) {
        Users users = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с таким id" + id + " не найден"));

        if (newPassword != null && !newPassword.isEmpty()) {
            if (currenPassword == null || currenPassword.isEmpty()) {
                throw new IllegalArgumentException("Должен предоставлен быть текущий пароль");
            }
            if (newPassword.length() < 8) {
                throw new IllegalArgumentException("Пароль не должен быть короче 8 символов");
            }
            if (!passwordEncoder.matches(currenPassword, users.getPassword())) {
                throw new IllegalArgumentException("Текущий пароль не верен");
            }
            users.setPassword(passwordEncoder.encode(newPassword));
        }
        userRepository.save(users);
    }

    @Transactional
    public void addAvatar(Long id, MultipartFile file) throws IOException {
        Users users = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с таким " + id + " не найден"));

        Image image = imageService.toImageEntity(file);
        image.setUsers(users);
        imageRepository.save(image);
        users.setAvatarId(image.getId());
        userRepository.save(users);
    }

    @Transactional
    public void addPhotos(Long id, List<MultipartFile> files) throws IOException {
        Users users = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с таким " + id + " не найден"));

        List<Image> images = users.getPhotos();
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                Image image = imageService.toImageEntity(file);
                image.setUsers(users);
                images.add(image);
            }
        }
        userRepository.save(users);
    }

    @Transactional
    public void updateAvatar(Long id, Long newAvatar) {
        Users users = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с таким " + id + " не найден"));
        users.setAvatarId(newAvatar);
        userRepository.save(users);
    }

    public UserRegistrationDTO findByEmail(String email) {
        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с такой почтой " + email + " не найден"));
        return userMapper.toDto(users);
    }

    @Transactional
    public void deletePhoto(Long id, Long photoId) {
        Users users = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователя с таким " + id + " не найдено"));

        Image imageFomDeleta = imageRepository.findById(photoId)
                .orElseThrow(() -> new IllegalArgumentException("Фото с таким " + id + " не найдно"));

        if (!imageFomDeleta.getUsers().getId().equals(id)) {
            throw new IllegalArgumentException("Фотография не принадлежит пользователю");
        }
        users.getPhotos().remove(imageFomDeleta);
        imageRepository.delete(imageFomDeleta);
        userRepository.save(users);
    }

    @Transactional
    public void updateRoles(Long id, Set<Role> newRole) {
        Users users = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователя с таким " + id + " не найденно"));

        if (newRole == null || newRole.isEmpty()) {
            throw new IllegalArgumentException("Роли не могут быть пустыми");
        }
        users.setRoles(newRole);
        userRepository.save(users);
        expireUserSessions(users.getEmail());
    }

    public void expireUserSessions(String username) {
        System.out.println("Попытка завершить сеансы для: " + username);
        // Прохожу  по всем принципалам в реестре сессий
        for (Object principal : sessionRegistry.getAllPrincipals()) {
            String principalName;

            // Определяю  тип принципала и получаю    имя пользователя
            if (principal instanceof org.springframework.security.core.userdetails.User) {
                principalName = ((org.springframework.security.core.userdetails.User) principal).getUsername();
            } else if (principal instanceof String) {
                principalName = (String) principal;
            } else if (principal instanceof Users) {
                principalName = ((Users) principal).getEmail();
            } else {

                continue;
            }
            System.out.println("Ищем principiall: " + principalName);

            // если нашёл нужного пользователя
            if (principalName.equals(username)) {
                List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
                System.out.println("Ищем " + sessions.size() + " сессии для юзера: " + username);

                // Завершаем все сессии
                sessions.forEach(session -> {
                    System.out.println("Завершаем сессии: " + session.getSessionId());
                    session.expireNow();
                });

                return; //выход после обработки
            }
        }

        System.out.println("Для пользователя не найдено ни одной сессии: " + username);
    }

    public List<Users> getAllUsers(){
        return userRepository.findAll();
    }

}
