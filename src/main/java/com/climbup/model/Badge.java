package com.climbup.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "badges")
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique badge code like "FIRST_TASK", "SEVEN_DAY_STREAK"
    @Column(nullable = false)
    private String code;

    // Human-readable name like "First Task Completed"
    @Column(nullable = false)
    private String name;

    // Icon URL or file name for UI
    private String icon;

    // Date unlocked
    private LocalDate unlockedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // ===== Constructors =====
    public Badge() {}

    public Badge(String code, String name, String icon, User user) {
        this.code = code;
        this.name = name;
        this.icon = icon;
        this.user = user;
        this.unlockedAt = LocalDate.now();
    }

    // ===== Getters & Setters =====
    public Long getId() { return id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public LocalDate getUnlockedAt() { return unlockedAt; }
    public void setUnlockedAt(LocalDate unlockedAt) { this.unlockedAt = unlockedAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
