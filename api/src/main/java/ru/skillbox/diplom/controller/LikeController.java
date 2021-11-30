package ru.skillbox.diplom.controller;

import org.springframework.web.bind.annotation.*;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.request.LikeBodyRequest;

@CrossOrigin
@RequestMapping("/api/v1/likes")
public interface LikeController {

    @PutMapping
    CommonResponse<?> putLike(@RequestBody LikeBodyRequest body);

    @DeleteMapping()
    CommonResponse<?> deleteLike(
            @RequestParam(name = "item_id") Long itemId,
            @RequestParam(name = "post_id", required = false) Long postId,
            @RequestParam() String type);
}
