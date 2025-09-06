package com.pooja.url_shortener_project.service;

import com.pooja.url_shortener_project.model.Url;
import com.pooja.url_shortener_project.repository.UrlRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final Random random = new Random();

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    // Method to generate short code
    private String generateShortCode() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // Save new URL with short code
    public Url shortenUrl(String originalUrl) {
        String shortCode = generateShortCode();
        Url url = new Url(originalUrl, shortCode);
        return urlRepository.save(url);
    }

    // Retrieve original URL from short code
    public Optional<Url> getOriginalUrl(String shortCode) {
        return urlRepository.findByShortCode(shortCode);
    }
}
