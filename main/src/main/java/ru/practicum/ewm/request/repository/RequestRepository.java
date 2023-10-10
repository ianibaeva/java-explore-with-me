package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByRequesterId(Long userId);

    Request findByEventIdAndRequesterId(Long eventId, Long userId);

    List<Request> findByEventInitiatorIdAndEventId(Long initiatorId, Long eventId);
}