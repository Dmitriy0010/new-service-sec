package ru.teachify.servicea;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProxyController {

    private final WebClient webClient;

    public ProxyController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping("/words")
    public Mono<List<String>> getWords() {
        // GET-запрос к Service B; токен вставляется автоматически фильтром
        return webClient.get()
                .uri("/api/words")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {});
    }
}
