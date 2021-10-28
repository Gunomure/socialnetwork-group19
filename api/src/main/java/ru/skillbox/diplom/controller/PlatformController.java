package ru.skillbox.diplom.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.skillbox.diplom.model.request.LanguageRequest;
import ru.skillbox.diplom.model.response.LanguageResponse;

@CrossOrigin
@RequestMapping("/api/v1/platform")
public interface PlatformController {

    @GetMapping("/languages")
    LanguageResponse getLanguage();
}
