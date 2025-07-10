package com.markDev.backend_biblioteca_springboot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GoogleBooksService {

    // Injeta o valor da chave que guardamos no application.properties
    @Value("${google.books.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String API_URL = "https://www.googleapis.com/books/v1/volumes";

    public String buscarLivrosExterno(String titulo) {
        String urlFormatada = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("q", titulo)
                .queryParam("key", apiKey)
                .queryParam("maxResults", 20)
                .toUriString();

        return restTemplate.getForObject(urlFormatada, String.class);
    }
}
