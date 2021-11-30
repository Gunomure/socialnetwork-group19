package ru.skillbox.diplom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.skillbox.diplom.model.Person;
import ru.skillbox.diplom.model.Post;
import ru.skillbox.diplom.model.PostLike;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long>, JpaSpecificationExecutor<PostLike> {
    Optional<PostLike> findByPostIdAndPersonId(Post postId, Person personId);
}
