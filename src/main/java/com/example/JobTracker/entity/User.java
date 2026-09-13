package com.example.JobTracker.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name="users")
public class User {
@Id
@GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
@Column(unique=true,nullable=false)
private String username;
@Column(unique=true,nullable=false)
    private String Email;
@NotNull
    private String password_Hash;
@NotNull
    @Enumerated(EnumType.STRING)
    private Role role;
@NotNull
private String created_at;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword_Hash() {
        return password_Hash;
    }

    public void setPassword_Hash(String password_Hash) {
        this.password_Hash = password_Hash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.created_at = (LocalDateTime.now()).format(formatter);
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
