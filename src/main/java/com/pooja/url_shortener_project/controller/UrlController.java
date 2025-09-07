package com.pooja.url_shortener_project.controller;

import com.pooja.url_shortener_project.model.Url;
import com.pooja.url_shortener_project.service.UrlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    // 1. POST /shorten  -> shorten a long URL
    @PostMapping("/shorten")
    public ResponseEntity<Map<String, String>> shortenUrl(@RequestBody Map<String, String> request) {
        String originalUrl = request.get("url");

        try{
            Url url = urlService.shortenUrl(originalUrl);

            return ResponseEntity.ok(Map.of(
                    "originalUrl", url.getOriginalUrl(),
                    "shortCode", url.getShortCode(),
                    "shortenedUrl", "http://localhost:8080/api/" + url.getShortCode()
            ));
        } catch (IllegalArgumentException e){
            //Handle invalid URL error
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e){
            //Catch-all for unexpected issues
            return ResponseEntity.internalServerError().body(Map.of("error","Something went wrong"));
        }
    }

    // 2. GET /{shortCode} -> redirect to original URL
    @GetMapping("/{shortCode}")
    public ResponseEntity<Object> redirect(@PathVariable String shortCode) {
        Optional<Url> urlOptional = urlService.getOriginalUrl(shortCode);

        if (urlOptional.isPresent()) {
            return ResponseEntity.status(302)
                    .location(URI.create(urlOptional.get().getOriginalUrl()))
                    .build();
        } else {
            //return ResponseEntity.notFound().build();
            return ResponseEntity.status(404).body(Map.of("error", "Short URL not found"));
        }
    }
}

