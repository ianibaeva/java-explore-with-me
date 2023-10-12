package ru.practicum.ewm.comment.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

@UtilityClass
public class CommentMapper {
    public static Comment toNewCommentDto(NewCommentDto dto, User user, Event event) {
        return Comment.builder()
                .id(dto.getId())
                .eventId(event)
                .userId(user)
                .text(dto.getText())
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .eventId(comment.getEventId().getId())
                .userId(comment.getUserId().getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .updated(comment.getUpdated())
                .build();
    }
}
