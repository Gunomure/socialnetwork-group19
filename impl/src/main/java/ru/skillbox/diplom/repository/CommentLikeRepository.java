package ru.skillbox.diplom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.skillbox.diplom.model.CommentLike;
import ru.skillbox.diplom.model.Person;
import ru.skillbox.diplom.model.PostComment;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long>, JpaSpecificationExecutor<CommentLike> {
    Optional<CommentLike> findByCommentIdAndPersonId(PostComment commentId, Person personId);
}
