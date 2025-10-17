package ru.teachify.servicea;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
@EnableCaching
public class WebClientConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager("clientTokens");
        manager.setCacheSpecification("maximumSize=100,expireAfterWrite=23h");
        return manager;
    }

    @Bean
    public TokenService tokenService(WebClient tokenWebClient) {
        return new TokenService(tokenWebClient);
    }

    @Bean
    public WebClient tokenWebClient() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(10));

        return WebClient.builder()
                .baseUrl("https://auth-server:9443")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Bean
    public WebClient webClient(TokenService tokenService) {
        ExchangeFilterFunction bearer = (request, next) -> {
            String token = tokenService.getClientCredentialsToken();
            return next.exchange(
                    ClientRequest.from(request)
                            .headers(h -> h.setBearerAuth(token))
                            .build()
            );
        };

        HttpClient httpClient = HttpClient.create().responseTimeout(Duration.ofSeconds(5));

        return WebClient.builder()
                .baseUrl("https://service-b:8443")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(bearer)
                .build();
    }
}
