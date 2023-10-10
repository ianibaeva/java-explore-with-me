package ru.practicum.ewm.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.stats.server.model.EndpointHit;
import ru.practicum.ewm.stats.server.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query(value = "SELECT NEW ru.practicum.ewm.stats.server.model.ViewStats(hit.uri, hit.app, COUNT(DISTINCT hit.ip)) " +
            "FROM EndpointHit AS hit " +
            "WHERE hit.timestamp BETWEEN ?1 AND ?2 " +
            "AND hit.uri IN (?3) " +
            "GROUP BY hit.app, hit.uri " +
            "ORDER BY COUNT(hit.ip) DESC")
    List<ViewStats> getStatsByUnique(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT NEW ru.practicum.ewm.stats.server.model.ViewStats(hit.uri, hit.app, COUNT(DISTINCT hit.ip)) " +
            "FROM EndpointHit AS hit " +
            "WHERE hit.uri IN (?3) " +
            "AND hit.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY hit.app, hit.uri, hit.ip " +
            "ORDER BY COUNT(DISTINCT hit.ip) DESC")
    List<ViewStats> getStatsByUrisAndUnique(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT NEW ru.practicum.ewm.stats.server.model.ViewStats(hit.uri, hit.app, COUNT(hit.ip)) " +
            "FROM EndpointHit AS hit " +
            "WHERE hit.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY hit.app, hit.uri " +
            "ORDER BY COUNT(hit.ip) DESC")
    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end);

    @Query("SELECT NEW ru.practicum.ewm.stats.server.model.ViewStats(hit.uri, hit.app, COUNT(hit.ip)) " +
            "FROM EndpointHit AS hit " +
            "WHERE hit.timestamp BETWEEN ?1 AND ?2 " +
            "AND hit.uri IN (?3) " +
            "GROUP BY hit.app, hit.uri " +
            "ORDER BY COUNT(hit.ip) DESC")
    List<ViewStats> getStatsByUris(LocalDateTime start, LocalDateTime end, List<String> uris);
}
