package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.enums.CommentSort;

import java.util.List;

public interface CommentService {
    CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto editComment(Long commentId, Long userId, NewCommentDto newCommentDto);

    void deleteComment(Long commentId, Long userId);

    List<CommentDto> getComments(Long eventId, CommentSort sort, Integer from, Integer size);

    void deleteCommentByAdmin(Long commentId);
}
