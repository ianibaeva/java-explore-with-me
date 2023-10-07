package ru.practicum.ewm.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.event.enums.State;
import ru.practicum.ewm.event.model.Event;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findEventsByInitiatorId(Long userId, Pageable pageable);

    List<Event> findAllByCategoryId(Long catId);

    Optional<Event> findEventByIdAndInitiator_Id(Long id, Long userId);

    @Query("SELECT e FROM Event AS e " +
            "JOIN e.category AS c " +
            "WHERE ((:text) IS NULL OR lower(e.annotation) LIKE lower(concat('%', :text, '%')) " +
            "OR lower(e.title) LIKE lower(concat('%', :text, '%'))) " +
            "AND (:categories IS NULL OR c.id IN :categories) " +
            "AND e.paid = :paid " +
            "AND e.eventDate > :rangeStart " +
            "AND (cast(:rangeEnd as date) IS NULL OR e.eventDate < :rangeEnd) " +
            "AND e.state = 'PUBLISHED' " +
            "ORDER BY e.eventDate")
    List<Event> findPublicSortByDate(String text, List<Long> categories, Boolean paid,
                                     LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    @Query("SELECT e FROM Event AS e " +
            "JOIN e.category AS c " +
            "WHERE ((:text) IS NULL OR lower(e.annotation) LIKE lower(concat('%', :text, '%')) " +
            "OR lower(e.title) LIKE lower(concat('%', :text, '%'))) " +
            "AND (:categories IS NULL OR c.id IN :categories) " +
            "AND e.paid = :paid " +
            "AND e.eventDate > :rangeStart " +
            "AND (cast(:rangeEnd as date) IS NULL OR e.eventDate < :rangeEnd) " +
            "AND e.state = 'PUBLISHED' " +
            "ORDER BY e.views DESC")
    List<Event> findPublicSortByViews(String text, List<Long> categories, Boolean paid,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    @Query("SELECT e FROM Event AS e " +
            "JOIN e.category AS c " +
            "JOIN e.initiator AS u " +
            "WHERE ((:users) IS NULL OR u.id IN :users) " +
            "AND ((:states) IS NULL OR e.state IN :states) " +
            "AND ((:categories) IS NULL OR c.id IN :categories) " +
            "AND e.eventDate > :rangeStart " +
            "AND (cast(:rangeEnd as date) IS NULL OR e.eventDate < :rangeEnd)")
    List<Event> findAdminEvents(List<Long> users, List<State> states, List<Long> categories,
                                LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    Event findEventByIdAndAndState(Long eventId, State state);

    Set<Event> getByIdIn(Collection<Long> ids);
}
