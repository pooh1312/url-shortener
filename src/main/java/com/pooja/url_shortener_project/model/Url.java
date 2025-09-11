package com.pooja.url_shortener_project.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000) //allow URLs up to 2000 chars
    private String originalUrl;

    @Column(length = 20)
    private String shortCode;

    @Column(nullable = false)
    private LocalDateTime createdAt; // when the URL was created

    private LocalDateTime expiryAt; // when the URL will expire (nullable = never expires)


    // Constructors
    public Url() {}

    public Url(String originalUrl, String shortCode, LocalDateTime createdAt,LocalDateTime expiryAt) {
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.createdAt = createdAt;
        this.expiryAt = expiryAt;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiryAt() {
        return expiryAt;
    }

    public void setExpiryAt(LocalDateTime expiryAt) {
        this.expiryAt = expiryAt;
    }

    // Helper method to check if expired
    public boolean isExpired() {
        return expiryAt != null && LocalDateTime.now().isAfter(expiryAt);
    }
}
