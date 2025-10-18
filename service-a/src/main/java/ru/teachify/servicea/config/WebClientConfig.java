package ru.teachify.servicea.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfig {

    private final OAuth2TokenService tokenService;
    private final String registrationId;

    public WebClientConfig(OAuth2TokenService tokenService,
                           @Value("${app.oauth2.registration-id:authclient}") String registrationId) {
        this.tokenService = tokenService;
        this.registrationId = registrationId;
    }

    /**
     * WebClient, который автоматически добавляет Authorization: Bearer <token>
     * Использует registrationId = app.oauth2.registration-id (напр. "authclient").
     */
    @Bean
    public WebClient webClient() {
        ExchangeFilterFunction bearerTokenFilter = ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            String token = tokenService.getAccessToken(registrationId);
            ClientRequest authorized = ClientRequest.from(clientRequest)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .build();
            return Mono.just(authorized);
        });

        return WebClient.builder()
                .filter(bearerTokenFilter)
                .build();
    }
}
