package ru.skillbox.diplom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.skillbox.diplom.model.PostComment;

public interface PostCommentRepository extends JpaRepository<PostComment, Long>, JpaSpecificationExecutor<PostComment> {
    PostComment findByIdAndIsBlocked(Long id, boolean isBlocked);
}
