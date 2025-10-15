package ru.teachify.servicea;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.web.reactive.function.client.WebClient;
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2Function) {
        oauth2Function.setDefaultClientRegistrationId("service-b");
        return WebClient.builder()
                .baseUrl("https://service-b:8443")
                .filter(oauth2Function)
                .build();
    }

    @Bean
    public ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2Client(
            ReactiveClientRegistrationRepository clientRegistrationRepository) {

        ReactiveOAuth2AuthorizedClientService authorizedClientService =
                new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrationRepository);

        ReactiveOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, authorizedClientService);

        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2Function =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth2Function.setDefaultClientRegistrationId("service-b");

        return oauth2Function;
    }
}
