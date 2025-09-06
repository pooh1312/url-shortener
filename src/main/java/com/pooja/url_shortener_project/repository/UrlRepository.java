package com.pooja.url_shortener_project.repository;

import java.util.Optional;
import com.pooja.url_shortener_project.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UrlRepository extends JpaRepository<Url, Long>{
    Optional<Url> findByShortCode(String shortCode);
}
