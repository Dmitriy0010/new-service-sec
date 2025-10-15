package ru.teachify.serviceb;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class WordController {

    @GetMapping("/words")
    public List<String> getWords() {
        return List.of("alpha", "beta", "gamma");
    }
}
