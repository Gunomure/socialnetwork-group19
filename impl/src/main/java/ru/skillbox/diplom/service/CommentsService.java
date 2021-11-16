package ru.skillbox.diplom.service;

import org.springframework.stereotype.Service;
import ru.skillbox.diplom.model.Person;
import ru.skillbox.diplom.model.Post;
import ru.skillbox.diplom.model.PostComment;
import ru.skillbox.diplom.repository.PostCommentRepository;

@Service
public class CommentsService {

    private final PostCommentRepository commentRepository;

    public CommentsService(PostCommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public PostComment addComment(Person person, PostComment parentComment, Post post, String text){
        PostComment comment = new PostComment();
        comment.setAuthorId(person);
        comment.setParentId(parentComment);
        comment.setPostId(post);
        comment.setCommentText(text);
        return commentRepository.save(comment);
    }
}
