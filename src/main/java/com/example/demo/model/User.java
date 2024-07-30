package com.example.demo.model;

import com.example.demo.utils.BcryptUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Pattern(
            regexp = "^[a-z0-9._-]+$",
            message = "Username must contain only small letters, digits, dots, underscores, and hyphens"
    )
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String role = "USER";

    @Email(message = "This is not a valid email")
    @Pattern(
            regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$",
            message = "Email is not valid"
    )
    @Column(nullable = false, unique = true)
    private String email;

    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
            message = "Password must be at least 8 characters long, contain at least one digit, one lowercase letter, one uppercase letter, one special character, and no whitespace"
    )
    @Column(nullable = false)
    private String password;

    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
            message = "Confirmation password must match the same criteria as the password"
    )
    @Transient
    private String conPassword;

    @Column
    private String confirmationCode;

    @Column
    private LocalDateTime confirmationCodeExpiry;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConPassword() {
        return conPassword;
    }

    public void setConPassword(String conPassword) {
        this.conPassword = conPassword;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public LocalDateTime getConfirmationCodeExpiry() {
        return confirmationCodeExpiry;
    }

    public void setConfirmationCodeExpiry(LocalDateTime confirmationCodeExpiry) {
        this.confirmationCodeExpiry = confirmationCodeExpiry;
    }
}
