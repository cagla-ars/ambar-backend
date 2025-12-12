package com.staj.dto;

import java.time.LocalDateTime;

public class UserDto {

    private Long id;
    private String username;
    private String role;
    private String email;
    private boolean tempPasswordUsed;
    private LocalDateTime createdAt; 

    public UserDto() {}

    public UserDto(Long id, String username, String role, String email, boolean tempPasswordUsed, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.email = email;
        this.tempPasswordUsed = tempPasswordUsed;
        this.createdAt = createdAt;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isTempPasswordUsed() { return tempPasswordUsed; }
    public void setTempPasswordUsed(boolean tempPasswordUsed) { this.tempPasswordUsed = tempPasswordUsed; }

    public LocalDateTime getCreatedAt() { return createdAt; } 
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; } 
}
