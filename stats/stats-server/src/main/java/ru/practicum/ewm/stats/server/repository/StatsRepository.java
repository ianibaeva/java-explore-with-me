package ru.practicum.ewm.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.stats.server.model.EndpointHit;
import ru.practicum.ewm.stats.server.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("SELECT NEW ru.practicum.ewm.stats.server.model.ViewStats(hit.app, hit.uri, COUNT(DISTINCT hit.ip)) " +
            "FROM EndpointHit AS hit " +
            "WHERE hit.timestamp BETWEEN :start AND :end " +
            "AND (:uris IS NULL OR hit.uri IN :uris) " +
            "GROUP BY hit.app, hit.uri ")
    List<ViewStats> getStatsByUrisAndUnique(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT NEW ru.practicum.ewm.stats.server.model.ViewStats(hit.app, hit.uri, COUNT(hit.ip)) " +
            "FROM EndpointHit AS hit " +
            "WHERE hit.timestamp BETWEEN :start AND :end " +
            "AND (:uris IS NULL OR hit.uri IN :uris) " +
            "GROUP BY hit.app, hit.uri ")
    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris);
}
