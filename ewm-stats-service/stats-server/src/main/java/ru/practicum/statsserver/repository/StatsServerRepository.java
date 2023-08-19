package ru.practicum.statsserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.statsserver.model.EndpointHit;
import ru.practicum.statsserver.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsServerRepository extends JpaRepository<EndpointHit, Long> {

    @Query("SELECT new ru.practicum.statsserver.model.ViewStats(eh.app, eh.uri, COUNT (DISTINCT eh.ip)) FROM EndpointHit AS eh " +
            "WHERE (eh.timestamp BETWEEN ?1 AND ?2) AND (eh.uri IN (?3) OR ?3 IS NULL) GROUP BY eh.uri ORDER BY COUNT (eh.ip) DESC")
    List<ViewStats> findAllViewStatsWithUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.statsserver.model.ViewStats(eh.app, eh.uri, COUNT (eh.ip)) FROM EndpointHit AS eh " +
            "WHERE (eh.timestamp BETWEEN ?1 AND ?2) AND (eh.uri IN (?3) OR ?3 IS NULL) GROUP BY eh.uri ORDER BY COUNT (eh.ip) DESC")
    List<ViewStats> findAllViewStatsWithoutUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);
}
