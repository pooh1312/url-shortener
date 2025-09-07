package com.pooja.url_shortener_project.service;

import com.pooja.url_shortener_project.model.Url;

public class ShortenResult {
    private final Url url;
    private final boolean isNew;

    public ShortenResult(Url url, boolean isNew) {
        this.url = url;
        this.isNew = isNew;
    }

    public Url getUrl() {
        return url;
    }

    public boolean isNew() {
        return isNew;
    }
}
