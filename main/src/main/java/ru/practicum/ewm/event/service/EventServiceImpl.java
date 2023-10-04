package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.enums.EventSortType;
import ru.practicum.ewm.event.enums.State;
import ru.practicum.ewm.event.enums.StateAction;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.stats.client.StatsClient;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.ewm.event.mapper.EventMapper.toEventUpdateUserRequest;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;

    @Override
    @Transactional
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        validateEventDate(newEventDto.getEventDate());
        Event event = EventMapper.toEvent(newEventDto);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format(
                        "User with ID: %s not found", userId
                )));
        event.setInitiator(user);

        event.setCreatedOn(LocalDateTime.now());

        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new ObjectNotFoundException(
                        "Category not found"
                ));
        event.setCategory(category);

        event.setState(State.PENDING);
        event.setConfirmedRequests(0);

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException(String.format(
                    "User with ID: %s was not found", userId
            ));
        }

        Pageable pageable = PageRequest.of(from, size);

        List<Event> list = eventRepository.findEventsByInitiatorId(userId, pageable);
        return list
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getUserEventById(Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException(String.format(
                    "User with ID: %s was not found", userId
            ));
        }

        Event event = eventRepository.findEventByIdAndInitiator_Id(eventId, userId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format(
                        "Event with ID: %s was not found", eventId
                )));

        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format(
                        "User with ID: %s was not found", userId
                )));

        Event event = eventRepository.findEventByIdAndInitiator_Id(eventId, userId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format(
                        "Event with ID: %s was not found", eventId
                )));

        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConflictException(String.format(
                    "User with ID: %s is not the initiator of event with ID: %s", userId, eventId
            ));
        }

        if (event.getInitiator().getId().equals(user.getId())) {
            if (updateEventUserRequest == null) {
                return EventMapper.toEventFullDto(event);
            }
            if (event.getPublishedOn() != null) {
                throw new ConflictException(
                        "Only 'PENDING' or 'CANCELED' events can be changed"
                );
            }
            if (updateEventUserRequest.getCategory() != null) {
                Category category = categoryRepository.findById(updateEventUserRequest.getCategory())
                        .orElseThrow(() -> new ObjectNotFoundException(
                                "Category not found"
                        ));
                event.setCategory(category);
            }
            if (updateEventUserRequest.getEventDate() != null) {
                validateEventDate(updateEventUserRequest.getEventDate());
                event.setEventDate(updateEventUserRequest.getEventDate());
            }
            if (updateEventUserRequest.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
                event.setState(State.PENDING);
            } else {
                event.setState(State.CANCELED);
            }
            toEventUpdateUserRequest(updateEventUserRequest, event);
        }
        eventRepository.save(event);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Boolean onlyAvailable, EventSortType sort,
                                               Integer from, Integer size, HttpServletRequest request) {

        List<Event> events = new ArrayList<>();

        validateEventDate(rangeEnd);

        if (rangeEnd.isBefore(rangeStart)) {
            throw new ValidationException(
                    "End of range cannot be before start"
            );
        }

        Pageable pageable = PageRequest.of(from, size);

        if (sort != null && sort.equals(EventSortType.EVENT_DATE)) {
            events = eventRepository.findPublicSortByDate
                    (text, categories, paid, rangeStart, rangeEnd, pageable);
        } else if (sort != null && sort.equals(EventSortType.VIEWS)) {
            events = eventRepository.findPublicSortByViews
                    (text, categories, paid, rangeStart, rangeEnd, pageable);
        }
        if (onlyAvailable) {
            events = events.stream()
                    .filter((event -> event.getParticipants().size() < event.getParticipantLimit()))
                    .collect(Collectors.toList());
        }
        List<EventShortDto> eventShortDtoList = events.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());

        while (eventShortDtoList.size() < size) {
            eventShortDtoList.add(new EventShortDto());
        }
        saveEndpointHit(request);
        return eventShortDtoList;
    }

    @Override
    public List<EventFullDto> getAdminEvents(List<Long> users, List<State> states,
                                             List<Long> categories, LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd, Integer from, Integer size) {

        validateEventDate(rangeEnd);

        Pageable pageable = PageRequest.of(from, size);

        return eventRepository.findAdminEvents
                        (users, states, categories, rangeStart, rangeEnd, pageable)
                .stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        if (updateEventAdminRequest.getEventDate() != null) {
            validateEventDate(updateEventAdminRequest.getEventDate());
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format(
                        "Event with ID: %s was not found", eventId
        )));

        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }

        if (updateEventAdminRequest.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
            if (event.getState().equals(State.PENDING)) {
                event.setState(State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else {
                throw new ConflictException(
                        "The event cannot be published due to its incorrect status: PENDING"
                );
            }
        }
        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction().equals(StateAction.REJECT_EVENT)) {
                if (event.getState().equals(State.PUBLISHED)) {
                    throw new ConflictException(
                            "The event cannot be published due to its incorrect status: PUBLISHED"
                    );
                } else {
                    event.setState(State.CANCELED);
                }
            }
        }
        eventRepository.save(event);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getPublicEventById(Long eventId, HttpServletRequest request) {
        saveEndpointHit(request);

        Event event = eventRepository.findEventByIdAndAndState(eventId, State.PUBLISHED);
        if (event == null) {
            throw new ObjectNotFoundException(String.format(
                    "Event with ID: %s was not found", eventId
            ));
        }

        if (event.getViews() == null) {
            event.setViews(0);
        }

        event.setViews(event.getViews() + 1);
        return EventMapper.toEventFullDto(event);
    }

    private void validateEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException(String.format(
                    "Event date (%s) must be at least 2 hours ahead of the current time", eventDate
            ));
        }
    }

    private void saveEndpointHit(HttpServletRequest request) {
        EndpointHitDto endpointHit = EndpointHitDto.builder()
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .app("main")
                .timestamp(LocalDateTime.now())
                .build();
        statsClient.addHit(endpointHit);
    }
}