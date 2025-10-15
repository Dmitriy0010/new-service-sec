package ru.teachify.authorizationserver;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
public class KeyManager {
    private final List<RSAKey> keys = new CopyOnWriteArrayList<>();

    public KeyManager() {
        // генерируем стартовый ключ
        rotate();
    }

    public synchronized RSAKey rotate() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair kp = kpg.generateKeyPair();
            RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) kp.getPublic())
                    .privateKey((RSAPrivateKey) kp.getPrivate())
                    .keyID(UUID.randomUUID().toString())
                    .build();
            keys.add(0, rsaKey); // newest first
            return rsaKey;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public synchronized boolean revoke(String kid) {
        return keys.removeIf(k -> k.getKeyID().equals(kid));
    }

    public JWKSet getJwkSet() {
        return new JWKSet(keys.stream().map(k -> (JWK) k).collect(Collectors.toList()));
    }

    public Optional<RSAKey> findByKid(String kid) {
        return keys.stream().filter(k -> k.getKeyID().equals(kid)).findFirst();
    }

    public RSAKey getCurrent() {
        return keys.get(0);
    }
}
