package ru.teachify.authorizationserver;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final KeyManager keyManager;

    public AdminController(KeyManager keyManager) { this.keyManager = keyManager; }

    @PostMapping("/rotate-key")
    public ResponseEntity<String> rotate() {
        var rsa = keyManager.rotate();
        return ResponseEntity.ok(rsa.getKeyID());
    }

    @PostMapping("/revoke/{kid}")
    public ResponseEntity<String> revoke(@PathVariable String kid) {
        boolean removed = keyManager.revoke(kid);
        return removed ? ResponseEntity.ok("revoked") : ResponseEntity.notFound().build();
    }
}
