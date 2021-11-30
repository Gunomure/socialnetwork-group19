package ru.skillbox.diplom.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.diplom.model.request.postRequest.CommentBodyRequest;
import ru.skillbox.diplom.model.request.postRequest.PostBodyRequest;

@CrossOrigin
//@PreAuthorize("hasAuthority('developers:read')")
@RequestMapping("/api/v1/post")
public interface PostController {

    @GetMapping
    ResponseEntity<?> getPosts(@RequestParam(name = "text", required = false) String text,
                           @RequestParam(name = "date_from", required = false) Long dateFrom,
                           @RequestParam(name = "date_to", required = false) Long dateTo,
                           @RequestParam(name = "autor", required = false) String author);
    @GetMapping(value = "/{id}")
    ResponseEntity<?> getPostById(@PathVariable Long id);

    @PutMapping(value = "/{id}")
    ResponseEntity<?> editPost(@PathVariable Long id, @RequestBody PostBodyRequest body);

    @DeleteMapping(value = "/{id}")
    ResponseEntity<?> deletePost(@PathVariable Long id);

    @GetMapping(value = "/{id}/comments")
    ResponseEntity<?> getCommentsById(@PathVariable Long id,
                                      @RequestParam (defaultValue = "0", required = false) Integer offset,
                                      @RequestParam (defaultValue = "10", required = false) Integer itemPerPage);

    @PostMapping(value = "/{id}/comments")
    ResponseEntity<?> createComment(@PathVariable Long id,
                                    @RequestBody CommentBodyRequest body);

    @PutMapping(value = "/{id}/comments/{comment_id}")
    ResponseEntity<?> editComment(@PathVariable Long id,
                                  @PathVariable(value = "comment_id") Long commentId,
                                  @RequestBody CommentBodyRequest body);

    @DeleteMapping(value = "/{id}/comments/{comment_id}")
    ResponseEntity<?> deleteComment(@PathVariable Long id,
                                    @PathVariable(value = "comment_id") Long commentId);

    @PutMapping(value = "/{id}/comments/{comment_id}/recover")
    ResponseEntity<?> recoverComment(@PathVariable Long id,
                                     @PathVariable(value = "comment_id") Long commentId);

    @PostMapping(value = "/{id}/comments/{comment_id}/report")
    ResponseEntity<?> createCommentReport(@PathVariable Long id,
                                          @PathVariable(value = "comment_id") Long commentId);
}
