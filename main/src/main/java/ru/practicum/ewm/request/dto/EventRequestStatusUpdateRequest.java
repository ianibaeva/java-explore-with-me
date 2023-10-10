package ru.practicum.ewm.request.dto;

import lombok.*;
import ru.practicum.ewm.request.enums.RequestStatus;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;

    private RequestStatus status;
}
