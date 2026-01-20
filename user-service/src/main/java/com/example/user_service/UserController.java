package com.example.user_service;

import com.example.user_service.dto.UserRegistrationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
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

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            UserRegistrationDTO userRegistrationDTO = userService.findByEmail(userDetails.getUsername());
            return ResponseEntity.ok(userRegistrationDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registrationUser (@RequestBody UserRegistrationDTO usersDto) {
        try {
            userService.createUsers(usersDto);
            return ResponseEntity.ok(usersDto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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
    public ResponseEntity<?> resetPasswordWithCode(@RequestParam String email,
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
    public ResponseEntity<?> sendEmailResetCode(@RequestParam String email,
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
    @PutMapping("update-user-password/{id}")
    public ResponseEntity<?> updateUserPassword(@PathVariable Long id,
                                                @RequestParam String newPassword,
                                                @RequestParam String currenPassword){
        try {
            userService.updateUserPassword(id , newPassword , currenPassword);
            return ResponseEntity.ok("Пароль успешно изменён");
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

@PostMapping("/confirm-email-change/{id}")
public ResponseEntity<?> confirmEmailChange(@PathVariable Long id,
                                            @RequestParam String code){
        try {
            userService.confirmEmailChange(id, code);
            return ResponseEntity.ok("Почта успешно изменена");
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
}


    @PostMapping("/addAvatar/{id}/newAvatar")
    public ResponseEntity<?> addAvatar(@PathVariable Long id,
                                       @RequestPart MultipartFile file) {
        try {
            userService.addAvatar(id, file);
            return ResponseEntity.ok("Аватарка успешно добавлена");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/addPhotos/{id}")
    public ResponseEntity<?> addPhotos(@PathVariable Long id,
                                       @RequestPart List<MultipartFile> files) {
        try {
            userService.addPhotos(id, files);
            return ResponseEntity.ok("Фотографии успешно добавлены");
        }catch (IOException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/updateAvatar/{id}/{newAvatar}")
    public ResponseEntity<?> updateAvatar(@PathVariable Long id ,
                                          @PathVariable Long newAvatar){
        try {
            userService.updateAvatar(id, newAvatar);
            return ResponseEntity.ok("Аватарка успешно обновлена");
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete-photo/{id}/{photoId}")
    public ResponseEntity<?> deletePhoto(@PathVariable Long id ,
                                         @PathVariable Long photoId){
        try {
            userService.deletePhoto(id , photoId);
            return ResponseEntity.ok("Фото успешно удалены");
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update-roles/{id}")
    public ResponseEntity<?> updateRoles(@PathVariable Long id ,
                                         @RequestBody Set<Role> newRole){
        try {
            userService.updateRoles(id , newRole);
            return ResponseEntity.ok("Роли успешно изменены");
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("get-all-users")
    public ResponseEntity<?> getAllUsers(){
      List <Users> users =  userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
