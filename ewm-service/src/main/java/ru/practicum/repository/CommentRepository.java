package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    boolean existsByIdAndCommentatorId(long commentId, long userId);

    Optional<Comment> findCommentByIdAndCommentatorId(long commentId, long userId);

    List<Comment> findAllByEventId(long eventId);

    List<Comment> findAllByCommentatorId(long userId);

    List<Comment> findAllByTextContainingIgnoreCase(String searchText);
}
