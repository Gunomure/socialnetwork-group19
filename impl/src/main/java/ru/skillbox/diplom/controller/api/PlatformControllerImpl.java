package ru.skillbox.diplom.controller.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.diplom.controller.PlatformController;
import ru.skillbox.diplom.model.request.LanguageRequest;
import ru.skillbox.diplom.model.response.LanguageResponse;
import ru.skillbox.diplom.service.LanguageService;

@RestController
public class PlatformControllerImpl implements PlatformController {
    private final static Logger LOGGER = LogManager.getLogger(PlatformControllerImpl.class);
    private final LanguageService languageService;

    public PlatformControllerImpl(LanguageService languageService) {
        this.languageService = languageService;
    }

    @Override
    public LanguageResponse getLanguage() {
        // @RequestBody LanguageRequest languageRequest
        // TODO delete hardcode
        LanguageRequest languageRequest = new LanguageRequest();
        languageRequest.setLanguage("lang");
        languageRequest.setOffset(0);
        languageRequest.setItemPerPage(10);
        // TODO delete hardcode
        LOGGER.info("Controller getLanguage: {}", languageRequest);
        return languageService.findLanguages(languageRequest);
    }
}
