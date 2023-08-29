package ru.practicum.statsserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.statsdto.EndpointHitDto;
import ru.practicum.statsdto.ViewStatsDto;
import ru.practicum.statsserver.exception.BadRequestException;
import ru.practicum.statsserver.mappers.EndpointHitMapper;
import ru.practicum.statsserver.mappers.ViewStatsMapper;
import ru.practicum.statsserver.model.EndpointHit;
import ru.practicum.statsserver.repository.StatsServerRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsServerRepository statsServerRepository;

    @Override
    public EndpointHitDto addHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = EndpointHitMapper.toEndpointHit(endpointHitDto);
        EndpointHit result = statsServerRepository.save(endpointHit);
        log.info("Добавлена информация с id-{}, что в сервис-{} на uri-{} " +
                        "был отправлен запрос пользователем с ip-{}, время отправления-{}",
                result.getId(), result.getApp(), result.getUri(), result.getIp(), result.getTimestamp());

        return EndpointHitMapper.toEndpointHitDto(result);
    }

    @Override
    public List<ViewStatsDto> findHits(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

        if (start.isAfter(end)) {
            throw new BadRequestException("Дата начало не должна быть позже конечной даты");
        }

        List<ViewStatsDto> result;
        if (unique) {
            result = statsServerRepository.findAllViewStatsWithUniqueIp(start, end, uris).stream()
                    .map(ViewStatsMapper::toViewStatsDto)
                    .collect(Collectors.toList());
            log.info("Получена статистика по уникальным посещениям по списку {}", uris);
        } else {
            result = statsServerRepository.findAllViewStatsWithoutUniqueIp(start, end, uris).stream()
                    .map(ViewStatsMapper::toViewStatsDto)
                    .collect(Collectors.toList());
            log.info("Получена статистика по всем посещениям по списку {}", uris);
        }
        return result;
    }

}
