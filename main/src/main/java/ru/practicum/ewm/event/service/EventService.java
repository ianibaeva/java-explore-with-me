package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.enums.EventSortType;
import ru.practicum.ewm.event.enums.State;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    EventFullDto addEvent(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size);

    EventFullDto getUserEventById(Long userId, Long eventId);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, Boolean onlyAvailable, EventSortType sort,
                                        Integer from, Integer size, HttpServletRequest request);

    List<EventFullDto> getAdminEvents(List<Long> users, List<State> states, List<Long> categories,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    EventFullDto getPublicEventById(Long eventId, HttpServletRequest request);
}
