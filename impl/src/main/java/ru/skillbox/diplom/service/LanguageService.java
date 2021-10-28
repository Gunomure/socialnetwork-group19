package ru.skillbox.diplom.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.skillbox.diplom.mappers.LanguageMapper;
import ru.skillbox.diplom.model.Language;
import ru.skillbox.diplom.model.LanguageDto;
import ru.skillbox.diplom.model.request.LanguageRequest;
import ru.skillbox.diplom.model.response.LanguageResponse;
import ru.skillbox.diplom.repository.LanguageRepository;
import ru.skillbox.diplom.util.TimeUtil;

import java.util.List;

@Service
public class LanguageService {
    private final static Logger LOGGER = LogManager.getLogger(LanguageService.class);
    private final LanguageRepository languageRepository;

    public LanguageService(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    public LanguageResponse findLanguages(LanguageRequest request) {
        LOGGER.info("start findLanguages: {}", request);

        PageRequest pageRequest = PageRequest.of(request.getOffset(), request.getItemPerPage(), Sort.by("language").ascending());
        List<Language> languagesByLanguageContaining =
                languageRepository.findByLanguageContaining(request.getLanguage(), pageRequest);
        List<LanguageDto> languageResponseList = LanguageMapper.getInstance().toLanguageDto(languagesByLanguageContaining);
        LanguageResponse response = new LanguageResponse();
        response.setOffset(request.getOffset());
        response.setPerPage(request.getItemPerPage());
        response.setTotal(languageResponseList.size());
        response.setData(languageResponseList);
        response.setTimestamp(TimeUtil.getCurrentTimestampUtc());

        LOGGER.info("finish findLanguages: {}", request);

        return response;
    }
}
