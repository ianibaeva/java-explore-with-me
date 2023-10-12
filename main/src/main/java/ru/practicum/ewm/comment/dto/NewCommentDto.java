package ru.practicum.ewm.comment.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCommentDto {
    private Long id;
    private Long eventId;
    private Long userId;
    private String text;
}
