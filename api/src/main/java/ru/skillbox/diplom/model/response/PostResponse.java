package ru.skillbox.diplom.model.response;

import lombok.Data;

@Data
public class PostResponse {
    private int total;
    private int offset;
    private int perPage;
}
