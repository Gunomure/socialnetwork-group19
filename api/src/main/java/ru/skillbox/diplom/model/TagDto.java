package ru.skillbox.diplom.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class TagDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;

    private String tag;
}
