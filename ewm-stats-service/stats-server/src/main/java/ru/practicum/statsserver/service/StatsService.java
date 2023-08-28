package ru.practicum.statsserver.service;

import ru.practicum.statsdto.EndpointHitDto;
import ru.practicum.statsdto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    EndpointHitDto addHit(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> findHits(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
