package com.staj.service;

import com.staj.dto.UserCreateDTO;
import com.staj.dto.UserDto;
import com.staj.entity.User;
import com.staj.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.staj.entity.Role;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.lang.NonNull;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final SecureRandom random = new SecureRandom();

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       MailService mailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }


    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    public UserDto getUserById(@NonNull Long id) {
        return userRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

public UserWithTempPassword createUser(UserCreateDTO dto) {

    User user = new User();
    user.setUsername(dto.getUsername());
    user.setEmail(dto.getEmail());
    user.setRole(Role.valueOf(dto.getRole().toUpperCase()));
    user.setTempPasswordUsed(false);
    user.setCreatedAt(LocalDateTime.now());

    String tempPassword = generateTempPassword();

    user.setPasswordHash(passwordEncoder.encode(tempPassword));

    User savedUser = userRepository.save(user);

    String subject = "Geçici Şifreniz";
    String body = "Merhaba " + savedUser.getUsername() + ",\n\n"
            + "Geçici şifreniz: " + tempPassword + "\n"
            + "Lütfen ilk girişte şifrenizi değiştirin.";

    mailService.sendMail(savedUser.getEmail(), subject, body);

    return new UserWithTempPassword(convertToDto(savedUser), tempPassword);
}



    public UserDto updateUser(@NonNull Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setUsername(updatedUser.getUsername());

                    if (updatedUser.getPasswordHash() != null && !updatedUser.getPasswordHash().isBlank()) {
                        user.setPasswordHash(passwordEncoder.encode(updatedUser.getPasswordHash()));
                    }

                    user.setRole(updatedUser.getRole());
                    user.setEmail(updatedUser.getEmail()); 

                    User savedUser = userRepository.save(user);
                    return convertToDto(savedUser);
                })
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }
    
    public void deleteUser(Long id) {
    if (!userRepository.existsById(id)) {
        throw new RuntimeException("Kullanıcı bulunamadı: " + id);
    }
    userRepository.deleteById(id);
}



    public UserDto login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid username or password");
        }

        return convertToDto(user);
    }


    private String generateTempPassword() {
        byte[] bytes = new byte[6];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

public void changePassword(@NonNull Long userId, String oldPassword, String newPassword) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

    if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
        throw new RuntimeException("Eski şifre yanlış");
    }

    user.setPasswordHash(passwordEncoder.encode(newPassword));
    user.setTempPasswordUsed(true); 
    userRepository.save(user);
}


private UserDto convertToDto(User user) {
    return new UserDto(
        user.getId(),
        user.getUsername(),
        user.getRole() != null ? user.getRole().toString() : null,
        user.getEmail(),
        user.isTempPasswordUsed(), 
        user.getCreatedAt()
    );
}



    public static class UserWithTempPassword {
        private UserDto user;
        private String tempPassword;

        public UserWithTempPassword(UserDto user, String tempPassword) {
            this.user = user;
            this.tempPassword = tempPassword;
        }

        public UserDto getUser() { return user; }
        public void setUser(UserDto user) { this.user = user; }

        public String getTempPassword() { return tempPassword; }
        public void setTempPassword(String tempPassword) { this.tempPassword = tempPassword; }
    }

    public void changePasswordByUsername(String username, String oldPassword, String newPassword) {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

    if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
        throw new RuntimeException("Mevcut şifre yanlış");
    }

    user.setPasswordHash(passwordEncoder.encode(newPassword));
    userRepository.save(user);
}

}
