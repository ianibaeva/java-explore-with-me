package ru.practicum.ewm.compilation.dto;

import lombok.*;

import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompilationRequest {
    private Set<Long> events;

    private Boolean pinned;

    @Size(max = 50, message = "Title cannot be more than 50 characters")
    private String title;
}
