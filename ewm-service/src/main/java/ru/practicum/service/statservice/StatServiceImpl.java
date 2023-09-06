package ru.practicum.service.statservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.model.Event;
import ru.practicum.repository.RequestRepository;
import ru.practicum.statsclient.client.StatClient;
import ru.practicum.statsdto.EndpointHitDto;
import ru.practicum.statsdto.ViewStatsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatServiceImpl implements StatService {
    private final RequestRepository requestRepository;

    private final ObjectMapper objectMapper;

    private final StatClient statClient;

    @Value("${main_app}")
    private String app;

    @Override
    public Map<Long, Long> getConfirmedRequests(List<Event> events) {
        List<Long> eventIds = events.stream()
                .map(e -> e.getId())
                .collect(Collectors.toList());

        List<ConfirmedRequests> requests = requestRepository.countByEventIdInAndStatusIsConfirmed(eventIds);
        Map<Long, Long> resultMap = requests.stream()
                .collect(Collectors.toMap(ConfirmedRequests::getEventId, ConfirmedRequests::getConfirmedRequestsCount));

        log.info("Получены количества одобренных заявок на события");
        return resultMap;
    }

    @Override
    public Map<Long, Long> getViews(List<Event> events) {

        LocalDateTime start = events.stream().map(e -> e.getCreatedOn()).min(LocalDateTime::compareTo).orElse(null);
        if (start == null) {
            return Map.of();
        }
        List<String> uris = events.stream().map(e -> "/events/" + e.getId()).collect(Collectors.toList());

        ResponseEntity<Object> response = statClient.getStats(start, LocalDateTime.now(), uris, true);

        Map<Long, Long> resultMap;

        try {
            List<ViewStatsDto> viewList = Arrays.asList(objectMapper.readValue(
                    objectMapper.writeValueAsString(response.getBody()), ViewStatsDto[].class));

            resultMap = viewList.stream()
                    .collect(Collectors.toMap(viewStatsDto -> Long.parseLong(viewStatsDto.getUri().replaceAll("[\\D]", "")),
                            ViewStatsDto::getHits));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка преобразование объекта");
        }
        log.info("Получены количества уникальных просмотров на события");

        return resultMap;
    }

    @Override
    public void addHits(HttpServletRequest request) {
        statClient.addPost(EndpointHitDto.builder()
                .app(app)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());
        log.info("Просмотр добавлен в статистику");
    }

}
