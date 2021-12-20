package ru.skillbox.diplom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.skillbox.diplom.model.Post;
import ru.skillbox.diplom.model.PostToTag;

import java.util.Optional;

public interface PostToTagRepository extends JpaRepository<PostToTag, Long>, JpaSpecificationExecutor<PostToTag> {

    Optional<PostToTag> findByTagTagNameAndPost(String tagName, Post post);

}
