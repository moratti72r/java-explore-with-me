package ru.practicum.statsserver.mappers;

import ru.practicum.statsdto.ViewStatsDto;
import ru.practicum.statsserver.model.ViewStats;

public class ViewStatsMapper {
    public static ViewStatsDto toViewStatsDto(ViewStats viewStats) {
        return ViewStatsDto.builder()
                .app(viewStats.getApp())
                .uri(viewStats.getUri())
                .hits(viewStats.getHits())
                .build();
    }
}
