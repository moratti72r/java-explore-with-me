package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.model.State;
import ru.practicum.service.eventservice.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping(path = "/admin")
public class AdminEventController {

    private final EventService eventService;

    @GetMapping("/events")
    public List<EventFullDto> getAllEvent(@RequestParam(name = "users", required = false) List<Long> users,
                                          @RequestParam(name = "states", required = false) List<State> states,
                                          @RequestParam(name = "categories", required = false) List<Long> categories,
                                          @RequestParam(name = "rangeStart", required = false)
                                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                          @RequestParam(name = "rangeEnd", required = false)
                                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                          @RequestParam(name = "from", defaultValue = "0") int from,
                                          @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Получен GET запрос /admin/events?users={}&states={}&categories={}&rangeStart={}&rangeEnd={}&from={}&size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        return eventService.getAllEventsForAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable long eventId,
                                    @RequestBody UpdateEventAdminRequest updateEvent) {
        log.info("Получен PATCH запрос /admin/events/{}", eventId);
        return eventService.updateEventByAdmin(eventId, updateEvent);
    }
}
