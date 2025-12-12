package com.staj.controller;

import com.staj.dto.UserDto;
import com.staj.entity.User;
import com.staj.service.UserService;
import com.staj.service.UserService.UserWithTempPassword;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import com.staj.dto.UserCreateDTO;


@RestController
@RequestMapping("/api/users") 
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
public ResponseEntity<List<UserDto>> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
}


    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable  Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

@PostMapping
public ResponseEntity<UserWithTempPassword> createUser(
        @Valid @RequestBody UserCreateDTO dto) {

    UserWithTempPassword result = userService.createUser(dto);


    URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(result.getUser().getId())
            .toUri();

    return ResponseEntity.created(location).body(result);
}


    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable  Long id, @RequestBody @Valid User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable  Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
