package ru.skillbox.diplom.model;

import lombok.Data;

@Data
public class CommonResponse<T> {
    private String error;
    private Long timestamp;
    private T data;
}