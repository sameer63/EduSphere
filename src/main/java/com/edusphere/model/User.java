package com.edusphere.model;

import java.time.LocalDateTime;

public class User {
    private Long id;
    private String name;
    private String email;
    private String passwordHash;
    private String role;
    private boolean active;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    public User(){}

    public User(Long id,
                String name,
                String email,
                String passwordHash,
                String role,
                boolean active,
                LocalDateTime created_at,
                LocalDateTime updated_at
                ){
        this.id=id;
        this.name=name;
        this.email=email;
        this.passwordHash=passwordHash;
        this.role=role;
        this.active=active;
        this.created_at=created_at;
        this.updated_at=updated_at;
    }

    //getters and setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return created_at;
    }
    public void setCreatedAt(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdatedAt() {
        return updated_at;
    }
    public void setUpdatedAt(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }
}
