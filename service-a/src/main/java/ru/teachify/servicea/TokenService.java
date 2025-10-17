package ru.teachify.servicea;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class TokenService {

    private final WebClient tokenWebClient;

    @Value("${spring.security.oauth2.client.provider.service-b.token-uri}")
    private String tokenUri;

    @Value("${spring.security.oauth2.client.registration.service-b.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.service-b.client-secret}")
    private String clientSecret;

    public TokenService(WebClient tokenWebClient) {
        this.tokenWebClient = tokenWebClient;
    }

    @Cacheable(cacheNames = "clientTokens", key = "'service-b'")
    public String getClientCredentialsToken() {
        String basic = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("scope", "serviceb.read");

        TokenResponse response = tokenWebClient.post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .headers(h -> h.set("Authorization", "Basic " + basic))
                .body(BodyInserters.fromFormData(form))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block();

        if (response == null || response.access_token == null) {
            throw new IllegalStateException("Failed to obtain access token");
        }
        return response.access_token;
    }

    public static class TokenResponse {
        public String access_token;
        public String token_type;
        public long expires_in;
        public String scope;
    }
}


