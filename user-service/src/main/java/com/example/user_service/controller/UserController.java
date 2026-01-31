package com.example.user_service.controller;

import com.example.user_service.Role;
import com.example.user_service.UserRepository;
import com.example.user_service.UserService;
import com.example.user_service.dto.PrivetUserProfileDto;
import com.example.user_service.dto.PublicUserProfileDto;
import com.example.user_service.dto.UserDto;
import com.example.user_service.dto.UserRegistrationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    private final UserRepository userRepository;

    /// не трогать во фронт, это для post-service и дальнейшей работы
    @GetMapping("/secondName")
    public ResponseEntity<UserDto> getUserBySecondName(@RequestParam String secondName) {
        return ResponseEntity.ok(userService.getUserBySecondName(secondName));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@RequestParam Long id) {
        return ResponseEntity.ok(userService.getInfoById(id));
    }

    @GetMapping("/email")
    public ResponseEntity<UserDto> getUserByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping("/me")
    public ResponseEntity<PrivetUserProfileDto> getMyProfile(@RequestParam String email) {
        try {
            PrivetUserProfileDto privetUserProfileDto = userService.getPrivetProfile(email);
            return ResponseEntity.ok(privetUserProfileDto);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<PublicUserProfileDto> findProfile(@RequestParam String email) {
        try {
            PublicUserProfileDto profileDto = userService.findPublicProfile(email);
            return ResponseEntity.ok(profileDto);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @PostMapping("/registration")
    public ResponseEntity<UserRegistrationDTO> registrationUser(@RequestBody UserRegistrationDTO usersDto) {
        try {
            userService.createUsers(usersDto);
            return ResponseEntity.ok(usersDto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/confirm-registration")
    public ResponseEntity<String> confirmRegistration(@RequestParam("code") String code) {

        boolean isEnabled = userService.confirmRegistration(code);

        if (isEnabled) {
            return ResponseEntity.ok("Аккаунт успешно подтверждён");
        }
        return ResponseEntity.badRequest().body("Неверный код регистрации!");
    }

    @PostMapping("/resend-confirm-registration")
    public ResponseEntity<String> resendConfirmationCode(@RequestParam String email) {
        try {
            userService.resendConfirmationCode(email);
            return ResponseEntity.ok("Повторно отправлен код для регистрации");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/send-password-resetCode")
    public ResponseEntity<String> sendPasswordResetCode(@RequestParam String email) {
        try {
            userService.sendPasswordResetCode(email);
            return ResponseEntity.ok("Код для сброса пароля отправлен на почту.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset-password-with-code")
    public ResponseEntity<String> resetPasswordWithCode(@RequestParam String email,
                                                        @RequestParam String code,
                                                        @RequestParam String newPassword) {
        try {
            userService.resetPasswordWithCode(email, code, newPassword);
            return ResponseEntity.ok("Пароль успешно изменён");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/send-email-reset-code")
    public ResponseEntity<String> sendEmailResetCode(@RequestParam String email,
                                                     @RequestParam String newEmail) {
        try {
            userService.sendEmailResetCode(email, newEmail);
            return ResponseEntity.ok("Код для смены почты отправлен");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend-email-resetCode")
    public ResponseEntity<String> resendEmailResetCode(@RequestParam String pendingEmail) {
        try {
            userService.resendEmailResetCode(pendingEmail);
            return ResponseEntity.ok("Код был повторно отправлен");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //тут мб нужен будет сделать через AuthenticationPrincipal
    @PutMapping("/update-user-password/{id}")
    public ResponseEntity<String> updateUserPassword(@PathVariable Long id,
                                                     @RequestParam String newPassword,
                                                     @RequestParam String currenPassword) {
        try {
            userService.updateUserPassword(id, newPassword, currenPassword);
            return ResponseEntity.ok("Пароль успешно изменён");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/confirm-email-change/{id}")
    public ResponseEntity<String> confirmEmailChange(@PathVariable Long id,
                                                     @RequestParam String code) {
        try {
            userService.confirmEmailChange(id, code);
            return ResponseEntity.ok("Почта успешно изменена");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/addAvatar/{id}/newAvatar")
    public ResponseEntity<String> addAvatar(@PathVariable Long id,
                                            @RequestPart("file") MultipartFile file) {
        try {
            userService.addAvatar(id, file);
            return ResponseEntity.ok("Аватарка успешно добавлена");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/addPhotos/{id}")
    public ResponseEntity<String> addPhotos(@PathVariable Long id,
                                            @RequestPart("files") List<MultipartFile> files) {
        try {
            userService.addPhotos(id, files);
            return ResponseEntity.ok("Фотографии успешно добавлены");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/updateAvatar/{id}/{newAvatar}")
    public ResponseEntity<String> updateAvatar(@PathVariable Long id,
                                               @PathVariable Long newAvatar) {
        try {
            userService.updateAvatar(id, newAvatar);
            return ResponseEntity.ok("Аватарка успешно обновлена");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete-photo/{id}/{photoId}")
    public ResponseEntity<String> deletePhoto(@PathVariable Long id,
                                              @PathVariable Long photoId) {
        try {
            userService.deletePhoto(id, photoId);
            return ResponseEntity.ok("Фото успешно удалены");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update-roles/{id}")
    public ResponseEntity<String> updateRoles(@PathVariable Long id,
                                              @RequestBody Set<Role> newRole) {
        try {
            userService.updateRoles(id, newRole);
            return ResponseEntity.ok("Роли успешно изменены");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get-all-users")
    public ResponseEntity<List<UserRegistrationDTO>> getAllUsers() {
        List<UserRegistrationDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/changeAccountStatus/{id}")
    public ResponseEntity<String> changeAccountStatus(@PathVariable Long id,
                                                      @RequestParam boolean newAccountStatus) {
        try {
            userService.changeAccountStatus(id, newAccountStatus);
            return ResponseEntity.ok("Всё прошло  успешно , статус аккаунт теперь " + newAccountStatus);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PutMapping("/accountBlocking/{id}")
    public ResponseEntity<String> accountBlocking(@PathVariable Long id,
                                                  @RequestParam boolean newAccountStatus){
        try {
            userService.accountBlocking(id, newAccountStatus);
            return ResponseEntity.ok("Всё прошло  успешно , статус аккаунт теперь " + newAccountStatus);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
