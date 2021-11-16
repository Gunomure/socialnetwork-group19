package ru.skillbox.diplom.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.diplom.controller.PostController;
import ru.skillbox.diplom.model.request.postRequest.CommentBodyRequest;
import ru.skillbox.diplom.model.request.postRequest.PostBodyRequest;
import ru.skillbox.diplom.model.request.postRequest.PostRequest;
import ru.skillbox.diplom.service.PostService;


@RestController
public class PostControllerImpl implements PostController {

    private final PostService postService;

    public PostControllerImpl(PostService postService) {
        this.postService = postService;
    }

    @Override
    public ResponseEntity<?> getPosts(String text, Long dateFrom, Long dateTo, String author){
        return postService.getPosts(text, dateFrom, dateTo, author);
    }
//    @GetMapping
//    public ResponseEntity<?> getPosts(PostRequest request){
//        return postService.getPosts(request);
//    }

    @Override
    public ResponseEntity<?> getPostById(@PathVariable Long id){
        return postService.getPostById(id);
    }

    @Override
    public ResponseEntity<?> editPost(@PathVariable Long id, @RequestBody PostBodyRequest body) {
        Long publishDate = null;
        return postService.editPost(id, publishDate, body);
    }

    @Override
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        return postService.deletePost(id);
    }

    @Override
    public ResponseEntity<?> getCommentsById(@PathVariable Long id, @RequestParam PostRequest request){
        return postService.getCommentsById(id, request);
    }

    @Override
    public ResponseEntity<?> createComment(@PathVariable Long id, @RequestBody CommentBodyRequest body){
        return postService.createComment(id, body);
    }

    @Override
    public ResponseEntity<?> editComment(@PathVariable Long id, @PathVariable(value = "comment_id") Long commentId, @RequestBody CommentBodyRequest body){
        return postService.editComment(id, commentId, body);
    }

    @Override
    public ResponseEntity<?> deleteComment(@PathVariable Long id, @PathVariable(value = "comment_id") Long commentId){
        return postService.deleteComment(id, commentId);
    }

    @Override
    public ResponseEntity<?> recoverComment(@PathVariable Long id, @PathVariable(value = "comment_id") Long commentId){
        return postService.recoverComment(id, commentId);
    }


//    @PutMapping(value = "/{id}/recover")
//    public ResponseEntity<?> editPost(@PathVariable Long id) {
//        return postService.recoverPost(id);
//    }
//
//    @PostMapping(value = "/{id}/report")
//    public ResponseEntity<?> createPostReport(@PathVariable Long id){
//        return postService.createPostReport(id);
//    }
//
//    @PostMapping(value = "/{id}/comments/{comment_id}/report")
//    public ResponseEntity<?> createCommentReport(@PathVariable Long id, @PathVariable(value = "comment_id") Long commentId){
//        return postService.createCommentReport(id, commentId);
//    }

}
