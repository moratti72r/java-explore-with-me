package ru.practicum.statsserver.service;

import ru.practicum.statsdto.EndpointHitDto;
import ru.practicum.statsdto.ViewStatsDto;

import java.util.List;

public interface StatsService {
    void addHit (EndpointHitDto endpointHitDto);
     List<ViewStatsDto> findHits (String start, String end, List<String> uris, Boolean unique);
}
