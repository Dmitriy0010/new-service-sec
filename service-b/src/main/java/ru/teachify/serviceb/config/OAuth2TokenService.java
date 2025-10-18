package ru.teachify.serviceb.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Service;

@Service
public class OAuth2TokenService {

    private final OAuth2AuthorizedClientManager authorizedClientManager;
    private final Authentication systemPrincipal;

    public OAuth2TokenService(OAuth2AuthorizedClientManager authorizedClientManager,
                              @Qualifier("systemPrincipal") Authentication systemPrincipal) {
        this.authorizedClientManager = authorizedClientManager;
        this.systemPrincipal = systemPrincipal;
    }

    /**
     * Возвращает access token для registrationId. Результат кешируется.
     * Ключ кеша: registrationId
     */
    @Cacheable(value = "oauth2Tokens", key = "#registrationId")
    public String getAccessToken(String registrationId) {
        OAuth2AuthorizeRequest req = OAuth2AuthorizeRequest.withClientRegistrationId(registrationId)
                .principal(systemPrincipal)
                .build();

        OAuth2AuthorizedClient client = authorizedClientManager.authorize(req);
        if (client == null || client.getAccessToken() == null) {
            throw new IllegalStateException("Не удалось получить access token для registrationId=" + registrationId);
        }
        return client.getAccessToken().getTokenValue();
    }
}
