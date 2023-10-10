package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.location.Location;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Location location;

    @Builder.Default
    private Boolean paid = false;

    @Builder.Default
    @PositiveOrZero
    private Long participantLimit = 0L;

    @Builder.Default
    private Boolean requestModeration = true;

    @NotBlank
    @Size(min = 3, max = 120)
    private String title;
}
