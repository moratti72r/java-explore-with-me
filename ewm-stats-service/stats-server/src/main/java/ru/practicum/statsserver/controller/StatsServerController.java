package ru.practicum.statsserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.statsdto.EndpointHitDto;
import ru.practicum.statsdto.ViewStatsDto;
import ru.practicum.statsserver.service.StatsService;

import javax.validation.Valid;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@Validated
public class StatsServerController {

    private final StatsService statsService;

    @PostMapping("/hit")
    @Validated
    public ResponseEntity<String> createHit(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        log.info("Получен POST запрос /hit");
        statsService.addHit(endpointHitDto);
        return ResponseEntity.ok("Информация сохранена");
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStatsDto>> getHits(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") String start,
                                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") String end,
                                                      @RequestParam(name = "uris", required = false) List<String> uris,
                                                      @RequestParam(name = "unique", defaultValue = "false") Boolean unique) {
        log.info("Получен GET запрос /stats?start={}&end={}&uris={}&unique={}", start, end, uris, unique);
        return ResponseEntity.ok(statsService.findHits(start, end, uris, unique));
    }
}
