package com.fashion.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")   // table name in MySQL
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    // ⭐ NEW: Role column (USER or ADMIN)
    @Column(nullable = false, length = 20)
    private String role = "USER";   // default role

    public User() {}

    public User(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = "USER"; // default for new registrations
    }

    // ==========================
    //       GETTERS & SETTERS
    // ==========================

    public Long getId() {
        return id;
    }
    public void setId(Long id) { this.id = id; }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() {
        return passwordHash;
    }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() {
        return role;
    }
    public void setRole(String role) { this.role = role; }
}


