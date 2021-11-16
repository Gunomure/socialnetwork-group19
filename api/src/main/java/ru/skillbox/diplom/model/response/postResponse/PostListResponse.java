package ru.skillbox.diplom.model.response.postResponse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.PostDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostListResponse extends CommonResponse<List<PostDto>> {
    private int total;
    private int offset;
    private int perPage;
}
