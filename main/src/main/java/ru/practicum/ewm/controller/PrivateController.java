package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.service.CommentService;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class PrivateController {

    private final EventService eventService;
    private final RequestService requestService;
    private final CommentService commentService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId,
                                 @RequestBody @Valid NewEventDto newEventDto) {
        return eventService.addEvent(userId, newEventDto);
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                             @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return eventService.getUserEvents(userId, from, size);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getUserEvent(@PathVariable Long userId,
                                     @PathVariable Long eventId) {
        return eventService.getUserEventById(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEventByUser(@PathVariable Long userId,
                                          @PathVariable Long eventId,
                                          @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        return eventService.updateEventByUser(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getRequestsById(@PathVariable Long userId) {
        return requestService.getRequestsById(userId);
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable Long userId,
                                              @RequestParam(required = false) Long eventId) {
        return requestService.addRequest(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId,
                                                 @PathVariable Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(@PathVariable Long userId,
                                                          @PathVariable Long eventId) {
        return requestService.getEventRequests(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateEventRequest(@PathVariable Long userId,
                                                             @PathVariable Long eventId,
                                                             @RequestBody EventRequestStatusUpdateRequest
                                                                         eventRequestStatusUpdateRequest) {

        return requestService.updateEventRequest(userId, eventId, eventRequestStatusUpdateRequest);
    }

    @PostMapping("/{userId}/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable Long userId,
                                 @PathVariable Long eventId,
                                 @RequestBody @Valid NewCommentDto newCommentDto) {
        return commentService.addComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{userId}/events/{eventId}/comments/{commentId}")
    public CommentDto editComment(@PathVariable Long commentId,
                                  @PathVariable Long userId,
                                  @RequestBody @Valid NewCommentDto newCommentDto) {
        return commentService.editComment(commentId, userId, newCommentDto);
    }

    @DeleteMapping("/{userId}/events/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId,
                              @PathVariable Long userId) {
        commentService.deleteComment(commentId, userId);
    }
}
