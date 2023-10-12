package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.event.enums.State;
import ru.practicum.ewm.location.Location;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {
    private Long id;

    private String annotation;

    private CategoryDto category;

    private Integer confirmedRequests;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private UserShortDto initiator;

    private Location location;

    private Boolean paid;

    @Builder.Default
    private Long participantLimit = 0L;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    @Builder.Default
    private Boolean requestModeration = true;

    private State state;

    private String title;

    private Integer views;

    private List<CommentDto> comments;
}
