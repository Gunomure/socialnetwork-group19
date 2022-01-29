package ru.skillbox.diplom.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.request.LikeBodyRequest;

@CrossOrigin
@RequestMapping("/api/v1/likes")
@PreAuthorize("hasAuthority('developers:read')")
public interface LikeController {

    @PutMapping
    CommonResponse<?> putLike(@RequestBody LikeBodyRequest body);

    @DeleteMapping()
    CommonResponse<?> deleteLike(@RequestBody LikeBodyRequest body);
}
