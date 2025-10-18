package ru.teachify.servicea.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final WebClient webClient;
    private final String serviceBUrl;

    public ApiController(WebClient webClient,
                         @Value("${app.peers.service-b-url}") String serviceBUrl) {
        this.webClient = webClient;
        this.serviceBUrl = serviceBUrl;
    }

    // Простой защищённый endpoint — вернёт информацию о текущем сервисе
    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("hello from service-a");
    }

    // Пример обращения к Service B (автоматически добавляется токен)
    @GetMapping("/call-service-b")
    public ResponseEntity<String> callServiceB() {
        String url = serviceBUrl + "/api/hello";
        String response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .block(); // в реальном приложении лучше избегать .block() и использовать reactive flow
        return ResponseEntity.ok("service-b replied: " + response);
    }
}
