package ru.skillbox.diplom.model.request;

import lombok.Data;

@Data
public class LanguageRequest {

    private String language; //Строка для поиска по языкам
    private int offset;
    private int itemPerPage;
}