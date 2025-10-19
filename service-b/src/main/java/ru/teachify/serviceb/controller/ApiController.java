package ru.teachify.serviceb.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@RestController
@RequestMapping("/api")
public class ApiController {

    private final WebClient webClient;
    private final String serviceBUrl;

    public ApiController(WebClient webClient,
                         @Value("${app.peers.service-a-url}") String serviceBUrl) {
        this.webClient = webClient;
        this.serviceBUrl = serviceBUrl;
    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        log.info("📨 Получен запрос /api/hello");
        return ResponseEntity.ok("hello from service-a");
    }

    @GetMapping("/call-service-b")
    public ResponseEntity<String> callServiceB() {
        String url = serviceBUrl + "/api/hello";
        log.info("🚀 Отправляю запрос к Service B: {}", url);
        try {
            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("✅ Успешно получили ответ от Service B: {}", response);
            return ResponseEntity.ok("service-b replied: " + response);
        } catch (Exception e) {
            log.error("❌ Ошибка при вызове Service B: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("Ошибка при вызове Service B: " + e.getMessage());
        }
    }
}
