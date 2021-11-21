package ru.skillbox.diplom.model.response.postResponse;

import lombok.*;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.PostCommentDto;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommentListResponse extends CommonResponse<List<PostCommentDto>> {

    private Integer offset;

    private Integer perPage;

    private Integer total;
}
