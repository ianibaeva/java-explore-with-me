package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.event.enums.StateAction;
import ru.practicum.ewm.location.Location;

import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventAdminRequest {
    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Location location;

    private Boolean paid;

    @PositiveOrZero
    private Long participantLimit;

    private Boolean requestModeration;

    private StateAction stateAction;

    @Size(min = 3, max = 120)
    private String title;
}
