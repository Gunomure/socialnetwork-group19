package ru.skillbox.diplom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.skillbox.diplom.model.PostComment;

import java.util.List;
import java.util.Optional;

public interface PostCommentRepository extends JpaRepository<PostComment, Long>, JpaSpecificationExecutor<PostComment> {

    Optional<PostComment> findByIdAndIsBlocked(Long id, boolean isBlocked);

    Optional<PostComment> findByIdAndIsBlockedAndPostId(Long id, boolean isBlocked, Long post);

    Optional<List<PostComment>> findByPostIdAndIsBlockedAndParentNull(Long id, boolean isBlocked);

    Optional<List<PostComment>> findByParentIdAndIsBlockedAndPostId(Long id, boolean isBlocked, Long post);
}
