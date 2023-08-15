package ru.practicum.statsserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.statsdto.ViewStatsDto;
import ru.practicum.statsserver.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsServerRepository extends JpaRepository<EndpointHit, Long> {

    @Query("SELECT new ru.practicum.statsdto.ViewStatsDto(eh.app, eh.uri, COUNT (DISTINCT eh.ip)) FROM EndpointHit AS eh " +
            "WHERE (eh.timestamp BETWEEN ?1 AND ?2) AND (eh.uri IN (?3)) GROUP BY eh.uri ORDER BY COUNT (eh.ip) DESC")
    List<ViewStatsDto> findAllViewStatsWithUrisAndUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.statsdto.ViewStatsDto(eh.app, eh.uri, COUNT (eh.ip)) FROM EndpointHit AS eh " +
            "WHERE (eh.timestamp BETWEEN ?1 AND ?2) AND (eh.uri IN (?3)) GROUP BY eh.uri ORDER BY COUNT (eh.ip) DESC")
    List<ViewStatsDto> findAllViewStatsWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.statsdto.ViewStatsDto(eh.app, eh.uri, COUNT (eh.ip)) FROM EndpointHit AS eh " +
            "WHERE (eh.timestamp BETWEEN ?1 AND ?2) GROUP BY eh.uri ORDER BY COUNT (eh.ip) DESC")
    List<ViewStatsDto> findAllViewStatsWithoutUris(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.statsdto.ViewStatsDto(eh.app, eh.uri, COUNT (DISTINCT eh.ip)) FROM EndpointHit AS eh " +
            "WHERE (eh.timestamp BETWEEN ?1 AND ?2) GROUP BY eh.uri ORDER BY COUNT (eh.ip) DESC")
    List<ViewStatsDto> findAllViewStatsWithoutUrisAndUniqueIp(LocalDateTime start, LocalDateTime end);
}
