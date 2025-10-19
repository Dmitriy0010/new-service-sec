package ru.teachify.servicea.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class WebClientConfig {

    private final OAuth2TokenService tokenService;
    private final String registrationId;

    public WebClientConfig(OAuth2TokenService tokenService,
                           @Value("${app.oauth2.registration-id:authclient}") String registrationId) {
        this.tokenService = tokenService;
        this.registrationId = registrationId;
    }

    @Bean
    public WebClient webClient() {
        ExchangeFilterFunction bearerTokenFilter = ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            String token = tokenService.getAccessToken(registrationId);
            log.debug("🔒 Добавляю токен для запроса: {}", clientRequest.url());
            ClientRequest authorized = ClientRequest.from(clientRequest)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .build();
            return Mono.just(authorized);
        });

        // логирование запросов и ответов
        ExchangeFilterFunction loggingFilter = ExchangeFilterFunction.ofRequestProcessor(request -> {
            log.info("➡️ Отправляю запрос: {} {}", request.method(), request.url());
            return Mono.just(request);
        }).andThen(ExchangeFilterFunction.ofResponseProcessor(response -> {
            log.info("⬅️ Ответ от сервиса: статус={}", response.statusCode());
            return Mono.just(response);
        }));

        return WebClient.builder()
                .filter(loggingFilter)
                .filter(bearerTokenFilter)
                .build();
    }
}
