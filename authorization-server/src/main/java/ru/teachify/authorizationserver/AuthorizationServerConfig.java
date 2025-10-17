package ru.teachify.authorizationserver;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Configuration
public class AuthorizationServerConfig {

    @Bean
	public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient serviceA = RegisteredClient.withId(UUID.randomUUID().toString())
				.clientId("service-a-client")
                .clientSecret("{noop}" + System.getenv().getOrDefault("SERVICE_A_SECRET", "dev-secret-a"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				.scope("serviceb.read")
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofDays(1))
                        .build())
                .build();

        RegisteredClient serviceB = RegisteredClient.withId(UUID.randomUUID().toString())
				.clientId("service-b")
                .clientSecret("{noop}" + System.getenv().getOrDefault("SERVICE_B_SECRET", "dev-secret-b"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				.scope("servicea.read")
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofDays(1))
                        .build())
                .build();

        return new InMemoryRegisteredClientRepository(serviceA, serviceB);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        return http.formLogin(Customizer.withDefaults()).build();
    }

	@Bean
	public JWKSource<SecurityContext> jwkSource(KeyManager keyManager) {
		JWKSet jwkSet = keyManager.getJwkSet();
		return new ImmutableJWKSet<>(jwkSet);
	}

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer("https://auth-server:9443")
                .build();
    }

	@Bean
	public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
		return context -> {
			JwtClaimsSet.Builder claims = context.getClaims();
			var scopes = context.getAuthorizedScopes();
			var audience = new java.util.ArrayList<String>();
			if (scopes.contains("serviceb.read")) {
				audience.add("service-b");
			}
			if (scopes.contains("servicea.read")) {
				audience.add("service-a");
			}
			if (!audience.isEmpty()) {
				claims.claim("aud", audience);
			}
		};
	}

	@Bean
	@Order(Ordered.LOWEST_PRECEDENCE)
	public SecurityFilterChain adminEndpointsSecurityFilterChain(HttpSecurity http) throws Exception {
		http
			.securityMatcher("/admin/**")
			.authorizeHttpRequests(authorize -> authorize.anyRequest().denyAll());
		return http.build();
	}
}
