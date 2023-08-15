package ru.practicum.statsserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.statsdto.EndpointHitDto;
import ru.practicum.statsdto.ViewStatsDto;
import ru.practicum.statsserver.model.EndpointHit;
import ru.practicum.statsserver.repository.StatsServerRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsServerRepository statsServerRepository;

    @Override
    public void addHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = EndpointHit
                .builder()
                .app(endpointHitDto.getApp())
                .uri(endpointHitDto.getUri())
                .ip(endpointHitDto.getIp())
                .timestamp(endpointHitDto.getTimestamp())
                .build();

        EndpointHit result = statsServerRepository.save(endpointHit);
        log.info("Добавлена информация с id-{}, что в сервис-{} на uri-{} " +
                        "был отправлен запрос пользователем с ip-{}, время отправления-{}",
                result.getId(), result.getApp(), result.getUri(), result.getIp(), result.getTimestamp());
    }

    @Override
    public List<ViewStatsDto> findHits(String start, String end, List<String> uris, Boolean unique) {
        String decoderStart = URLDecoder.decode(start, StandardCharsets.UTF_8);
        String decoderEnd = URLDecoder.decode(end, StandardCharsets.UTF_8);

        LocalDateTime startDate = LocalDateTime.parse(decoderStart);
        LocalDateTime endDate = LocalDateTime.parse(decoderEnd);

        if (unique) {
            if (uris != null && !uris.isEmpty()) {
                log.info("Получена статистика по уникальным посещениям по списку {}", uris);
                return statsServerRepository.findAllViewStatsWithUrisAndUniqueIp(startDate, endDate, uris);
            } else {
                log.info("Получена полная статистика по уникальным посещениям");
                return statsServerRepository.findAllViewStatsWithoutUrisAndUniqueIp(startDate, endDate);
            }
        } else {
            if (uris != null && !uris.isEmpty()) {
                log.info("Получена статистика по всем посещениям по списку {}", uris);
                return statsServerRepository.findAllViewStatsWithUris(startDate, endDate, uris);
            } else {
                log.info("Получена полная статистика по всем посещениям");
                return statsServerRepository.findAllViewStatsWithoutUris(startDate, endDate);
            }
        }
    }

}
