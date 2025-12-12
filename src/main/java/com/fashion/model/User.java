package com.fashion.model;

import java.time.LocalDate;

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

    // ⭐ New: Subscription Status (ACTIVE OR INACTIVE)
    @Column(nullable = false, length = 20)
    private String SubscriptionStatus = "INACTIVE";   // Default: no subscription

    // ⭐ NEW: Subscription Plan (basic, pro, etc.)
    @Column(length = 50)
    private String subscriptionPlan;

    // ⭐ NEW: Subscription Expiration Date
    @Column
    private LocalDate subscriptionEndDate;

    public User() {}

    public User(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = "USER"; 
        this.SubscriptionStatus = "INACTIVE";   // default for new registrations
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

    // ⭐ Subscription getters/setters
    public String getSubscriptionStatus() {
        return SubscriptionStatus;
    }
    public void setSubscriptionStatus(String subscriptionStatus) {
        this.SubscriptionStatus = subscriptionStatus;
    }

    public String getSubscriptionPlan() {
        return subscriptionPlan;
    }
    public void setSubscriptionPlan(String subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }

    public LocalDate getSubscriptionEndDate() {
        return subscriptionEndDate;
    }
    public void setSubscriptionEndDate(LocalDate subscriptionEndDate) {
        this.subscriptionEndDate = subscriptionEndDate;
    }
}


