package ru.skillbox.diplom.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.diplom.controller.PostController;
import ru.skillbox.diplom.model.request.postRequest.CommentBodyRequest;
import ru.skillbox.diplom.model.request.postRequest.PostBodyRequest;
import ru.skillbox.diplom.service.PostService;


@RestController
public class PostControllerImpl implements PostController {

    private final PostService postService;

    public PostControllerImpl(PostService postService) {
        this.postService = postService;
    }

    @Override
    public ResponseEntity<?> getPosts(String text,
                                      Long dateFrom,
                                      Long dateTo,
                                      String author,
                                      String tagQuery,
                                      Integer offset,
                                      Integer itemPerPage){
        return ResponseEntity.ok(postService.getPosts(text,
                dateFrom,
                dateTo,
                author,
                tagQuery,
                offset,
                itemPerPage));
    }

    @Override
    public ResponseEntity<?> getPostById(@PathVariable Long id){
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @Override
    public ResponseEntity<?> editPost(@PathVariable Long id, @RequestBody PostBodyRequest body) {
        Long publishDate = null;
        return ResponseEntity.ok(postService.editPost(id, publishDate, body));
    }

    @Override
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.deletePost(id));
    }

    @Override
    public ResponseEntity<?> getCommentsById(@PathVariable Long id,
                                             @RequestParam (defaultValue = "0", required = false) Integer offset,
                                             @RequestParam (defaultValue = "10", required = false) Integer itemPerPage){
        return ResponseEntity.ok(postService.getCommentsById(id, offset, itemPerPage));
    }

    @Override
    public ResponseEntity<?> createComment(@PathVariable Long id,
                                           @RequestBody CommentBodyRequest body){
        return ResponseEntity.ok(postService.createComment(id, body));
    }

    @Override
    public ResponseEntity<?> editComment(@PathVariable Long id,
                                         @PathVariable(value = "comment_id") Long commentId,
                                         @RequestBody CommentBodyRequest body){
        return ResponseEntity.ok(postService.editComment(id, commentId, body));
    }

    @Override
    public ResponseEntity<?> deleteComment(@PathVariable Long id,
                                           @PathVariable(value = "comment_id") Long commentId){
        return ResponseEntity.ok(postService.deleteComment(id, commentId));
    }

    @Override
    public ResponseEntity<?> recoverComment(@PathVariable Long id,
                                            @PathVariable(value = "comment_id") Long commentId){
        return ResponseEntity.ok(postService.recoverComment(id, commentId));
    }

    @PostMapping(value = "/{id}/comments/{comment_id}/report")
    public ResponseEntity<?> createCommentReport(@PathVariable Long id,
                                                 @PathVariable(value = "comment_id") Long commentId){
        return ResponseEntity.ok(postService.createCommentReport(id, commentId));
    }

}
