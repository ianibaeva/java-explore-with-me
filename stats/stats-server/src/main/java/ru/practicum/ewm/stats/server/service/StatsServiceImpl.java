package ru.practicum.ewm.stats.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.stats.server.mapper.StatsMapper;
import ru.practicum.ewm.stats.server.model.EndpointHit;
import ru.practicum.ewm.stats.server.model.ViewStats;
import ru.practicum.ewm.stats.server.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.stats.server.mapper.EndpointHitMapper.toEndpointHit;
import static ru.practicum.ewm.stats.server.mapper.EndpointHitMapper.toEndpointHitDto;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public EndpointHitDto create(EndpointHitDto endpointHitDto) {
        EndpointHit hit = toEndpointHit(endpointHitDto);
        return toEndpointHitDto(statsRepository.save(hit));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime startDate = LocalDateTime.parse(start, FORMATTER);
        LocalDateTime endDate = LocalDateTime.parse(end, FORMATTER);

        List<ViewStats> result;
        if (unique) {
            result = statsRepository.getStatsByUrisAndUnique(startDate, endDate, uris);
        } else {
            result = statsRepository.getStats(startDate, endDate, uris);
        }

        return result.stream()
                .map(StatsMapper::toStatsDto)
                .sorted(Comparator.comparing(ViewStatsDto::getHits).reversed())
                .collect(Collectors.toList());
    }
}
