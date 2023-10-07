package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.enums.State;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.enums.RequestStatus;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.ewm.request.mapper.RequestMapper.toParticipationRequestDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestsById(Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "User with ID: %s was not found", userId
                )));

        List<Request> requests = requestRepository.findByRequesterId(userId);

        return requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {

        if (eventId == null || eventId == 0) {
            throw new BadRequestException("Event ID is required");
        }

        User user = userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "User with ID: %s was not found", userId
                )));

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "Event with ID: %s was not found", eventId
                )));

        Request requestExist = requestRepository.findByEventIdAndRequesterId(eventId, userId);

        if (event.getState() != State.PUBLISHED) {
            throw new ConflictException(
                    "Unable to submit a request for an unpublished request"
            );
        }

        if (!Objects.isNull(requestExist)) {
            throw new ConflictException(String.format(
                    "Event with ID: %s and requester with ID: %s already exist", eventId, userId
            ));
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException(String.format(
                    "Event initiator with ID: %s is unable to submit a request for their event", userId
            ));
        }

        if (event.getParticipantLimit() != 0 &&
                event.getParticipants().size() >= event.getParticipantLimit()) {
            throw new ConflictException(
                    "The request has exceeded the maximum participant limit"
            );
        }

        Request request = Request.builder()
                .requester(user)
                .event(event)
                .created(LocalDateTime.now())
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        return toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "User with ID: %s was not found", userId
                )));

        Request request = requestRepository.findById(requestId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "Request with ID: %s was not found", requestId
                )));

        if (!user.getId().equals(request.getRequester().getId())) {
            throw new ConflictException(String.format(
                    "The request with ID: %s cannot be canceled by user with ID: %s", userId, requestId
            ));
        }

        request.setStatus(RequestStatus.CANCELED);

        return toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        List<Request> requests = requestRepository.findByEventInitiatorIdAndEventId(userId, eventId);

        return requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateEventRequest(Long userId, Long eventId,
                                                             EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        Event event = eventRepository.findEventByIdAndInitiator_Id(eventId, userId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "Event with ID: %s was not found", eventId
                )));

        if (event.getParticipants().size() >= event.getParticipantLimit()) {
            throw new ConflictException(
                    "The maximum limit of participants has been reached"
            );
        }

        if (event.getState() != State.PUBLISHED) {
            throw new ConflictException(
                    "Unable to join an unpublished event"
            );
        }

        List<Request> requests = requestRepository.findAllById(eventRequestStatusUpdateRequest.getRequestIds());

        requests.forEach(r -> {
            if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
                return;
            }
            if (RequestStatus.REJECTED == eventRequestStatusUpdateRequest.getStatus()) {
                r.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(toParticipationRequestDto(r));
            }
            if (RequestStatus.CONFIRMED == eventRequestStatusUpdateRequest.getStatus()) {
                r.setStatus(RequestStatus.CONFIRMED);
                confirmedRequests.add(toParticipationRequestDto(r));
            }
        });

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }
}
