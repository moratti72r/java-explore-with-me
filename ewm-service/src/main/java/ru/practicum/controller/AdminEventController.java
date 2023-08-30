package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.constants.DateTimePattern;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.model.State;
import ru.practicum.service.eventservice.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/admin")
@Validated
public class AdminEventController {

    private final EventService eventService;

    @GetMapping("/events")
    public List<EventFullDto> getAllEvent(@RequestParam(name = "users", required = false) List<Long> users,
                                          @RequestParam(name = "states", required = false) List<State> states,
                                          @RequestParam(name = "categories", required = false) List<Long> categories,
                                          @RequestParam(name = "rangeStart", required = false)
                                          @DateTimeFormat(pattern = DateTimePattern.pattern) LocalDateTime rangeStart,
                                          @RequestParam(name = "rangeEnd", required = false)
                                          @DateTimeFormat(pattern = DateTimePattern.pattern) LocalDateTime rangeEnd,
                                          @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                          @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        log.info("Получен GET запрос /admin/events?users={}&states={}&categories={}&rangeStart={}&rangeEnd={}&from={}&size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        return eventService.getAllEventsForAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable long eventId,
                                    @Valid @RequestBody UpdateEventAdminRequest updateEvent) {
        log.info("Получен PATCH запрос /admin/events/{}", eventId);
        return eventService.updateEventByAdmin(eventId, updateEvent);
    }
}
