package ru.teachify.serviceb.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OAuth2TokenService {

    private final OAuth2AuthorizedClientManager authorizedClientManager;
    private final Authentication systemPrincipal;

    public OAuth2TokenService(OAuth2AuthorizedClientManager authorizedClientManager,
                              @Qualifier("systemPrincipal") Authentication systemPrincipal) {
        this.authorizedClientManager = authorizedClientManager;
        this.systemPrincipal = systemPrincipal;
    }

    @Cacheable(value = "oauth2Tokens", key = "#registrationId")
    public String getAccessToken(String registrationId) {
        log.info("🔑 Получаю access token для registrationId='{}'", registrationId);
        OAuth2AuthorizeRequest req = OAuth2AuthorizeRequest.withClientRegistrationId(registrationId)
                .principal(systemPrincipal)
                .build();

        OAuth2AuthorizedClient client = authorizedClientManager.authorize(req);

        if (client == null || client.getAccessToken() == null) {
            log.error("❌ Не удалось получить access token для registrationId='{}'", registrationId);
            throw new IllegalStateException("Не удалось получить access token для registrationId=" + registrationId);
        }

        log.info("✅ Успешно получен access token (expires at: {})",
                client.getAccessToken().getExpiresAt());
        return client.getAccessToken().getTokenValue();
    }
}
