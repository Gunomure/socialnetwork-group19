package ru.skillbox.diplom.controller.api;

import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.diplom.controller.LikeController;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.request.LikeBodyRequest;
import ru.skillbox.diplom.service.PostService;

@RestController
public class LikeControllerImpl implements LikeController {

    private final PostService postService;

    public LikeControllerImpl(PostService postService) {
        this.postService = postService;
    }

    @Override
    public CommonResponse<?> putLike(LikeBodyRequest body) {
        return postService.putLike(body);
    }

    @Override
    public CommonResponse<?> deleteLike(LikeBodyRequest body) {
        return postService.deleteLike(body);
    }
}
