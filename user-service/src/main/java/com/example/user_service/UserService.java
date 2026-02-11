package com.example.user_service;

import com.example.user_service.dto.*;
import com.example.user_service.dto.mapping.UserMapper;
import com.example.user_service.dto.mapping.UserMapperNew;
import com.example.user_service.image.Image;
import com.example.user_service.image.ImageRepository;
import com.example.user_service.image.ImageService;
import com.example.user_service.kafka.EmailKafkaProducer;
import com.example.user_service.security.jwt.JwtService;
import com.example.user_service.security.jwt.RedisJwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;
    private final ImageRepository imageRepository;
    private final UserMapper userMapper;
    private final UserMapperNew userMapperNew;
    private final JwtService jwtService;
    private final RedisJwtService redisJwtService;
    private final EmailKafkaProducer emailKafkaProducer;




    public UserDto getUserBySecondName(String secondName) {
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
        Users users = userMapper.toEntity(userDto);
        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        LocalDateTime expireDate = LocalDateTime.now().plusMinutes(1);
        users.setTtlEmailCode(expireDate);

        users.setRoles(Set.of(Role.ROLE_USER));
        users.setPassword(passwordEncoder.encode(users.getPassword()));
        users.setConfirmationCode(code);
        userRepository.save(users);

        EmailRequestDto emailRequestDto = new EmailRequestDto();
        emailRequestDto.setTo(users.getEmail());
        emailRequestDto.setCode(users.getConfirmationCode());
        emailRequestDto.setType(EmailRequestDto.EmailType.CONFIRMATION);
        emailKafkaProducer.sendEmailToKafka(emailRequestDto);
    }

    @Transactional
    public boolean confirmRegistration(String code) {
        Optional<Users> usersOptional = userRepository.findByConfirmationCode(code);

        if (usersOptional.isPresent() && !LocalDateTime.now().isAfter(usersOptional.get().getTtlEmailCode())) {
            Users users = usersOptional.get();
            users.setEnable(true);
            users.setAccountNonLocked(true);
            users.setTtlEmailCode(null);
            users.setConfirmationCode(null);
            userRepository.save(users);
            return true;
        }
        return false;
    }

    public JwtAuthenticationDto singIn(UserCredentialsDto userCredentialsDto) throws AuthenticationException {
        Users users = findByCredentials(userCredentialsDto);
        if (users.getEnable() == true && users.isAccountNonLocked() && jwtService.validateJwtToken(users.getRefreshToken())) {
            return jwtService.refreshBaseToken(users.getId(), users.getEmail(), users.getRoles(), true, true  , users.getRefreshToken());
        }

        if (users.getEnable() == true && users.isAccountNonLocked()) {
            JwtAuthenticationDto jwtAuthenticationDto = jwtService.generateAuthToken(users.getId(), users.getEmail(), users.getRoles(), true, true);
            users.setRefreshToken(jwtAuthenticationDto.getRefreshToken());
            userRepository.save(users);
            return jwtAuthenticationDto;
        }  else {
            throw new IllegalArgumentException("Пользователь не подтвердил почту!");
        }
    }

    private Users findByCredentials(UserCredentialsDto userCredentialsDto) throws AuthenticationException {
        Optional<Users> optionalUsers = userRepository.findByEmail(userCredentialsDto.getEmail());
        if (optionalUsers.isPresent()) {
            Users users = optionalUsers.get();
            if (passwordEncoder.matches(userCredentialsDto.getPassword(), users.getPassword())) {
                return users;
            }
        }
        throw new AuthenticationException("Почта или пароль неверны");
    }

    public JwtAuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto) throws Exception {
        String refreshToken = refreshTokenDto.getRefreshToken();
        if (refreshToken != null && jwtService.validateJwtToken(refreshToken)) {
            Users users = findByEmail(jwtService.getEmailFromToken(refreshToken));
            return jwtService.refreshBaseToken(users.getId(),users.getEmail(), users.getRoles(), users.getEnable(), users.isAccountNonLocked(), refreshToken);
        }
        throw new AuthenticationException("Недействительный рефреш токен");
    }

    public void logout(String accessToken){
        long ttl = jwtService.getTimeFromToken(accessToken);
        if(ttl > 0){
            redisJwtService.saveTokenToBlackList(accessToken , ttl);
        }
    }

    @Transactional
    public void fullLogout(Long userId, String accessToken){
        Users users = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        users.setRefreshToken(null);
        userRepository.save(users);

        long ttl = jwtService.getTimeFromToken(accessToken);

        redisJwtService.saveTokenToBlackList(accessToken , ttl);
    }

//    private void banUser(Long userId){
//        Users users = userRepository.findById(userId)
//                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
//        users.setAccountNonLocked(false);
//        users.setRefreshToken(null);
//        userRepository.save(users);
//
//        redisJwtService.blockUserId(userId);
//    }

    //надо мб допилить, на скорую руку писал
    public JwtAuthenticationDto updateRefreshToken(String refreshToken) throws AuthenticationException {
        String email = jwtService.getEmailFromToken(refreshToken);

        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        if (!jwtService.validateJwtToken(users.getEmail())) {
            throw new AuthenticationException("Недействительный рефреш токен");
        }

        if (!refreshToken.equals(users.getRefreshToken())) {
            throw new AuthenticationException("Неверный refresh токен");
        }
        JwtAuthenticationDto newTokens = new JwtAuthenticationDto();

        String tokenAccess = String.valueOf(jwtService.generateAuthToken(users.getId(), users.getEmail(), users.getRoles(), users.getEnable(), users.isAccountNonLocked()));
        String refreshRefreshToken = String.valueOf(jwtService.refreshRefreshToken(users.getEmail()));
        newTokens.setToken(tokenAccess);
        newTokens.setRefreshToken(refreshRefreshToken);

        users.setRefreshToken(newTokens.getRefreshToken());
        userRepository.save(users);
        return newTokens;
    }

    @Transactional
    public void resendConfirmationCode(String email) {
        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с такой почтой не найден"));

        if (users.getEnable()) {
            throw new IllegalArgumentException("Аккаунт уже активирован");
        }
        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        LocalDateTime time = LocalDateTime.now().plusMinutes(15);
        users.setTtlEmailCode(time);

        users.setConfirmationCode(code);
        userRepository.save(users);

        EmailRequestDto emailRequestDto = new EmailRequestDto();
        emailRequestDto.setTo(users.getEmail());
        emailRequestDto.setCode(users.getConfirmationCode());
        emailRequestDto.setType(EmailRequestDto.EmailType.PASSWORD_RESET);
        emailKafkaProducer.sendEmailToKafka(emailRequestDto);
    }

    @Transactional
    public void sendPasswordResetCode(String email) {
        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с такой почтой не найден"));

        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        LocalDateTime time = LocalDateTime.now().plusMinutes(15);
        users.setPasswordResetCodeExpiryDate(time);

        users.setPasswordResetCode(code);
        userRepository.save(users);

        EmailRequestDto emailRequestDto = new EmailRequestDto();
        emailRequestDto.setTo(users.getEmail());
        emailRequestDto.setCode(users.getPasswordResetCode());
        emailRequestDto.setType(EmailRequestDto.EmailType.PASSWORD_RESET);
        emailKafkaProducer.sendEmailToKafka(emailRequestDto);
    }

    @Transactional
    public void resetPasswordWithCode(String email, String code, String newPassword) {
        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с такой почтой не найден"));

        if (users.getPasswordResetCode() == null || !users.getPasswordResetCode().equals(code) || LocalDateTime.now().isAfter(users.getPasswordResetCodeExpiryDate())) {
            throw new IllegalArgumentException("Неверный или просроченный  код для сброса пароля");
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

        EmailRequestDto emailRequestDto = new EmailRequestDto();
        emailRequestDto.setTo(users.getEmail());
        emailRequestDto.setCode(users.getConfirmationCode());
        emailRequestDto.setType(EmailRequestDto.EmailType.CONFIRMATION);
        emailKafkaProducer.sendEmailToKafka(emailRequestDto);
    }

    @Transactional
    public void resendEmailResetCode(String pendingEmail) {
        Users users = userRepository.findByPendingEmail(pendingEmail)
                .orElseThrow(() -> new IllegalArgumentException("Почта не найдена"));

        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        LocalDateTime time = LocalDateTime.now().plusMinutes(15);
        users.setTtlEmailCode(time);

        users.setEmailChangeCode(code);
        userRepository.save(users);

        EmailRequestDto emailRequestDto = new EmailRequestDto();
        emailRequestDto.setTo(users.getEmail());
        emailRequestDto.setCode(users.getConfirmationCode());
        emailRequestDto.setType(EmailRequestDto.EmailType.CONFIRMATION);
        emailKafkaProducer.sendEmailToKafka(emailRequestDto);
    }

    @Transactional
    public void confirmEmailChange(Long id, String code) {
        Users users = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с таким id" + id + " не найден"));

        if (users.getEmailChangeCode() == null || !users.getEmailChangeCode().equals(code) || LocalDateTime.now().isAfter(users.getTtlEmailCode())) {
            throw new IllegalArgumentException("Неверный или истёкший код подтверждения.");
        }

        users.setEmail(users.getPendingEmail());

        users.setTtlEmailCode(null);
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


    private Users findByEmail(String email) throws Exception {
        return userRepository.findByEmail(email).orElseThrow(() -> new Exception(String.format("Пользователя с такой почтой %s не найдено", email)));
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
        jwtService.refreshBaseToken(users.getId(), users.getEmail(), users.getRoles(), users.getEnable(), users.isAccountNonLocked(), users.getRefreshToken());
        userRepository.save(users);

    }


    public List<UserRegistrationDTO> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }


    @Transactional
    public void changeAccountStatus(Long id, boolean newAccountStatus) {
        Users users = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        users.setEnable(newAccountStatus);
        users.setRefreshToken(null);
        userRepository.save(users);

        redisJwtService.blockUserId(id);
    }

    @Transactional
    public void accountBlocking(Long id, boolean newAccountStatus) {
        Users users = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));


        users.setAccountNonLocked(newAccountStatus);
        users.setRefreshToken(null);
        userRepository.save(users);

        if(!newAccountStatus){
            redisJwtService.blockUserId(id);
        }
        if(newAccountStatus){
            redisJwtService.unblockUserId(id);
        }
    }

    @Transactional(readOnly = true)
    public PrivetUserProfileDto getPrivetProfile(Long id) {
        Users users = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        return userMapper.toPrivetProfielDto(users);
    }

    @Transactional(readOnly = true)
    public PublicUserProfileDto findPublicProfile(String email) {
        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        return userMapper.toPublicProfileDto(users);
    }

    @Transactional
    public void updateName(Long id , String newName){
        Users users = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        users.setName(newName);
        userRepository.save(users);
    }

    @Transactional
    public void updateSecondName(Long id , String newSecondName){
        Users users = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        users.setSecondName(newSecondName);
        userRepository.save(users);
    }

}
