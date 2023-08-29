package ru.practicum.statsserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statsdto.EndpointHitDto;
import ru.practicum.statsdto.ViewStatsDto;
import ru.practicum.statsserver.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StatsServerController {

    private final StatsService statsService;

    @PostMapping("/hit")
    @Validated
    public ResponseEntity<EndpointHitDto> createHit(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        log.info("Получен POST запрос /hit");
        return new ResponseEntity<>(statsService.addHit(endpointHitDto), HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStatsDto>> getHits(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                                      @RequestParam(name = "uris", required = false) List<String> uris,
                                                      @RequestParam(name = "unique", defaultValue = "false") Boolean unique) {
        log.info("Получен GET запрос /stats?start={}&end={}&uris={}&unique={}", start, end, uris, unique);
        return ResponseEntity.ok(statsService.findHits(start, end, uris, unique));
    }
}
