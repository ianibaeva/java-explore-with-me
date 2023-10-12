package ru.practicum.ewm.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.enums.CommentSort;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.event.enums.State;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.comment.mapper.CommentMapper.toCommentDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;

    public static final Sort SORT_BY_CREATED_ON_ASC = Sort.by(Sort.Direction.ASC, "created");
    public static final Sort SORT_BY_CREATED_ON_DESC = Sort.by(Sort.Direction.DESC, "created");

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format(
                        "User with ID: %s was not found", userId
                )));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format(
                        "Event with ID: %s was not found", eventId
                )));

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ValidationException(String.format(
                    "Event with ID: %s is not published to leave a comment", eventId
            ));
        }

        Comment comment = Comment.builder()
                .text(newCommentDto.getText())
                .created(LocalDateTime.now())
                .eventId(event)
                .userId(user)
                .build();

        Comment savedComment = commentRepository.save(comment);
        return toCommentDto(savedComment);
    }

    @Override
    @Transactional
    public CommentDto editComment(Long commentId, Long userId, NewCommentDto newCommentDto) {
        Comment toUpdateComment = commentRepository.findById(commentId).orElseThrow(() ->
                new ObjectNotFoundException(String.format(
                        "Comment with ID: %s not found", commentId
                )));

        User author = userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format(
                        "User with ID: %s not found", userId
                )));

        if (!toUpdateComment.getUserId().getId().equals(author.getId())) {
            throw new ConflictException(String.format(
                    "User with ID: %s is not the author of comment with ID: %s", userId, commentId
            ));
        }

        toUpdateComment.setText(newCommentDto.getText());
        toUpdateComment.setUpdated(LocalDateTime.now());
        Comment savedComment = commentRepository.save(toUpdateComment);
        return toCommentDto(savedComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format(
                        "Comment with ID: %s not found", commentId
                )));

        if (!comment.getUserId().getId().equals(userId)) {
            throw new ForbiddenException("You do not have permission to delete this comment");
        }

        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public void deleteCommentByAdmin(Long commentId) {
        if (commentRepository.existsById(commentId)) {
            commentRepository.deleteById(commentId);
        } else {
            throw new ObjectNotFoundException(String.format(
                    "Comment with ID: %s not found", commentId
            ));
        }
    }


    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getComments(Long eventId, CommentSort sort, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        List<Comment> comments;

        switch (sort) {
            case ASC:
                pageable = PageRequest.of(from, size, SORT_BY_CREATED_ON_ASC);
                comments = commentRepository.findAllByEventId(eventId, pageable);
                break;
            case DESC:
                pageable = PageRequest.of(from, size, SORT_BY_CREATED_ON_DESC);
                comments = commentRepository.findAllByEventId(eventId, pageable);
                break;
            default:
                comments = commentRepository.findAllByEventId(eventId, pageable);
        }

        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}
