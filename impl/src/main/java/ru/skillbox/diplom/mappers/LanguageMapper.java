package ru.skillbox.diplom.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import ru.skillbox.diplom.model.Language;
import ru.skillbox.diplom.model.LanguageDto;
import ru.skillbox.diplom.model.response.LanguageResponse;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface LanguageMapper {

    static LanguageMapper getInstance() {
        return Mappers.getMapper(LanguageMapper.class);
    }

    @Mappings({
            @Mapping(target = "id", source = "language.id"),
            @Mapping(target = "title", source = "language.language")
    })
    LanguageDto toLanguageDto(Language language);

    List<LanguageDto> toLanguageDto(Collection<Language> languages);

    @Mappings({
            @Mapping(target = "id", source = "languageResponse.id"),
            @Mapping(target = "language", source = "languageResponse.title")
    })
    Language toLanguageEntity(LanguageDto languageResponse);
}
