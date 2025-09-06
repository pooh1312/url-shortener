package com.pooja.url_shortener_project.model;

import jakarta.persistence.*;

@Entity
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000) //allow URLs up to 2000 chars
    private String originalUrl;

    @Column(length = 20)
    private String shortCode;

    // Constructors
    public Url() {}

    public Url(String originalUrl, String shortCode) {
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
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

}
