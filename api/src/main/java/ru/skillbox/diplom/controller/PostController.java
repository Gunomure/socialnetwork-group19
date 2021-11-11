package ru.skillbox.diplom.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.skillbox.diplom.model.response.LanguageResponse;

@CrossOrigin
@RequestMapping("/api/v1/post")
public interface PostController {

    @GetMapping("/")
    LanguageResponse getPost();
}
