package com.pooja.url_shortener_project.service;

import com.pooja.url_shortener_project.model.Url;
import com.pooja.url_shortener_project.repository.UrlRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

import java.net.MalformedURLException;
import java.net.URL;

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

    // Validate the URL format
    private boolean isValidUrl(String url) {
        if (url == null || !(url.startsWith("http://") || url.startsWith("https://"))) {
            return false;
        }
        try {
            new URL(url); // tries parsing, throws exception if invalid
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    // Save new URL with short code
    public ShortenResult shortenUrl(String originalUrl) {
        // Validation
        if (originalUrl == null || !isValidUrl(originalUrl)) {
            throw new IllegalArgumentException("Invalid URL provided");
        }

        //Duplicate check
        Optional<Url> existing = urlRepository.findAll().stream()
                .filter(u -> u.getOriginalUrl().equals(originalUrl))
                .findFirst();

        if (existing.isPresent()) {
            //return existing.get(); // return existing short code
            return new ShortenResult(existing.get(), false); // not new
        }

        // Otherwise generate a new short code
        String shortCode = generateShortCode();
        Url url = new Url(originalUrl, shortCode);
        Url saved = urlRepository.save(url);
        //return urlRepository.save(url);
        return new ShortenResult(saved,true);
    }

    // Retrieve original URL from short code
    public Optional<Url> getOriginalUrl(String shortCode) {
        return urlRepository.findByShortCode(shortCode);
    }
}
