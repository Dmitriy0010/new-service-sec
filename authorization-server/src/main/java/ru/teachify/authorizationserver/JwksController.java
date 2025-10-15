package ru.teachify.authorizationserver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.teachify.authorizationserver.KeyManager;

import java.util.Map;

@RestController
public class JwksController {
    private final KeyManager keyManager;
    public JwksController(KeyManager keyManager) { this.keyManager = keyManager; }

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> keys() {
        return keyManager.getJwkSet().toJSONObject();
    }
}
