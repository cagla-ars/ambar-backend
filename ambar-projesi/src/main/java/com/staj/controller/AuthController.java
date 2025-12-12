package com.staj.controller;

import com.staj.dto.LoginRequest;
import com.staj.entity.Role;
import com.staj.entity.User;
import com.staj.repository.UserRepository;
import com.staj.security.JwtUtil;
import com.staj.service.MailService;
import com.staj.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/auth")
public class AuthController {


    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final MailService mailService;


    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public AuthController(UserService userService, UserRepository userRepository, JwtUtil jwtUtil, MailService mailService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.mailService = mailService;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {


        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Kullanıcı bulunamadı");
        }

        User user = userOpt.get();

        if (!user.getPasswordHash().startsWith("$2a$")) {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
            userRepository.save(user);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Geçersiz şifre");
        }


        String token = jwtUtil.generateToken(
            user.getUsername(),
            "ROLE_" + user.getRole().name()  
        );

        return ResponseEntity.ok(
            Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "role", user.getRole(),
                "token", token,
                "message", "Login başarılı!"
            )
        );
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {


        String username = body.get("username");
        String email = body.get("email");
        String roleStr = body.get("role");


        if (username == null || email == null || roleStr == null) {
            throw new RuntimeException("Eksik bilgi");
        }


        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Kullanıcı adı zaten mevcut");
        }

        Role role;

        try {
            role = Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Geçersiz rol. Sadece ADMIN veya USER olabilir.");
        }

        String tempPassword = generateTempPassword();


        User newUser = new User(username, passwordEncoder.encode(tempPassword), role, email);
        userRepository.save(newUser);


        String subject = "Geçici Şifreniz";
        String bodyMessage = "Merhaba " + username + ",\n\nGeçici şifreniz: " + tempPassword +
                             "\nLütfen ilk girişte şifrenizi değiştirin.";
        mailService.sendMail(email, subject, bodyMessage);

        return ResponseEntity.ok("Kayıt başarılı! Geçici şifre mail olarak gönderildi.");
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {


        String username = userDetails.getUsername();

        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");



            userService.changePasswordByUsername(username, oldPassword, newPassword);
            return ResponseEntity.ok("Şifre başarıyla değiştirildi.");
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {

        String email = body.get("email");

        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Email bulunamadı");
        }

        User user = userOpt.get();

        String tempPassword = generateTempPassword();
        user.setPasswordHash(passwordEncoder.encode(tempPassword));
        user.setTempPasswordUsed(false); 
        userRepository.save(user);

        String subject = "Yeni Geçici Şifreniz";
        String message = "Merhaba " + user.getUsername() + ",\n\nYeni geçici şifreniz: " + tempPassword;
        mailService.sendMail(user.getEmail(), subject, message);

        return ResponseEntity.ok("Geçici şifre mail olarak gönderildi.");
    }

    private String generateTempPassword() {
        byte[] array = new byte[6];
        new java.security.SecureRandom().nextBytes(array);
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(array);
    }

}

