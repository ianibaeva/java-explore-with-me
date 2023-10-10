package ru.practicum.ewm.stats.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.stats.server.service.StatsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto create(@RequestBody EndpointHitDto endpointHitDto) {
        return statsService.create(endpointHitDto);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStatsDto> getStats(@RequestParam("start") String start,
                                       @RequestParam("end") String end,
                                       @RequestParam(value = "uris", defaultValue = "") List<String> uris,
                                       @RequestParam(value = "unique", defaultValue = "false") Boolean unique) {
        return statsService.getStats(start, end, uris, unique);
    }
}
