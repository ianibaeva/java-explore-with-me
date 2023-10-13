package ru.practicum.ewm.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByEventId(Long eventId, Pageable pageable);

    @Query("SELECT c FROM Comment AS c " +
            "JOIN c.eventId AS e " +
            "WHERE e.id = ?1")
    List<Comment> getCommentsByEventId(Long eventId);
}