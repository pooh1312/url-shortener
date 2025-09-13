package com.pooja.url_shortener_project.service;

import com.pooja.url_shortener_project.model.Url;
import com.pooja.url_shortener_project.repository.UrlRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import java.net.MalformedURLException;
import java.net.URL;

@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final RedisTemplate<String, String> redisTemplate; // Redis
    private final Random random = new Random();

    public UrlService(UrlRepository urlRepository,RedisTemplate<String, String> redisTemplate) {
        this.urlRepository = urlRepository;
        this.redisTemplate = redisTemplate;
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
    public ShortenResult shortenUrl(String originalUrl, Integer expiryInSeconds) {
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
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiryAt = null;

        if (expiryInSeconds != null && expiryInSeconds > 0) {
            expiryAt = createdAt.plusSeconds(expiryInSeconds);
        }

        Url url = new Url(originalUrl, shortCode, createdAt, expiryAt);
        Url saved = urlRepository.save(url);

        // Add to Redis cache
        redisTemplate.opsForValue().set(shortCode, originalUrl, Duration.ofHours(1));


        //return urlRepository.save(url);
        return new ShortenResult(saved,true);
    }

    // Retrieve original URL from short code (with expiry check)
    public Optional<Url> getOriginalUrl(String shortCode) {
        // 1️⃣ Check Redis first
        String cachedUrl = redisTemplate.opsForValue().get(shortCode);
        if (cachedUrl != null) {
            Url url = new Url(cachedUrl, shortCode, null, null); // dummy createdAt/expiry for Redis hits
            return Optional.of(url);
        }

        // 2️⃣ If not in cache, check DB
        //return urlRepository.findByShortCode(shortCode);
        Optional<Url> urlOpt = urlRepository.findByShortCode(shortCode);
        if (urlOpt.isPresent()) {
            Url url = urlOpt.get();
            if (url.isExpired()) {
                // expired → delete it or just block access
                urlRepository.delete(url);
                return Optional.empty();
            }
            // 3️⃣ Store in Redis for future requests
            redisTemplate.opsForValue().set(shortCode, url.getOriginalUrl(), Duration.ofHours(1));
            return Optional.of(url);

        }
        return Optional.empty();
    }
}
