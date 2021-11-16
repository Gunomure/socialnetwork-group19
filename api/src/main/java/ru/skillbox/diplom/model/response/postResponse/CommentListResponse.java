package ru.skillbox.diplom.model.response.postResponse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.PostCommentDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CommentListResponse extends CommonResponse<List<PostCommentDto>> {
}
