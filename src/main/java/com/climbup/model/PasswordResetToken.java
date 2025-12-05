package com.climbup.model;

import java.time.LocalDateTime;
import jakarta.persistence.*; // assuming you're using JPA/Hibernate

@Entity
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime expiryDate;

    public PasswordResetToken() {
        // default constructor for JPA
    }

    public PasswordResetToken(String token, User user, LocalDateTime expiryDate) {
        this.token = token;
        this.user = user;
        this.expiryDate = expiryDate;
    }

    // ---------- Getters ----------
    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    // ---------- Utility ----------
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}
