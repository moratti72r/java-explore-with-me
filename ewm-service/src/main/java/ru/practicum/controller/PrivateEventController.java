package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.service.eventservice.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/users")
@Validated
public class PrivateEventController {

    private final EventService eventService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable long userId,
                                    @Valid @RequestBody NewEventDto eventDto) {
        log.info("Получен POST запрос /users/{}/events", userId);
        return eventService.createEvent(userId, eventDto);
    }

    @GetMapping("/{userId}/events")
    public List<EventFullDto> getAllEvents(@PathVariable long userId,
                                           @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                           @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Получен GET запрос /users/{}/events", userId);
        return eventService.getAllEventsForInitiator(userId, from, size);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventById(@PathVariable long userId,
                                     @PathVariable long eventId) {
        log.info("Получен POST запрос /users/{}/events/{}", userId, eventId);
        return eventService.getEventByIdForInitiator(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable long userId,
                                    @PathVariable long eventId,
                                    @Valid @RequestBody UpdateEventUserRequest updateEvent) {
        log.info("Получен PATCH запрос /users/{}/events/{}", userId, eventId);
        return eventService.upDateEventByInitiator(userId, eventId, updateEvent);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequests(@PathVariable long userId,
                                                     @PathVariable long eventId) {
        log.info("Получен GET запрос /users/{}/events/{}/requests", userId, eventId);
        return eventService.getRequestByInitiatorAndEvent(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateStatusRequest(@PathVariable long userId,
                                                              @PathVariable long eventId,
                                                              @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        log.info("Получен PATCH запрос /users/{}/events/{}/requests", userId, eventId);
        return eventService.upDateStatusRequestsByInitiator(userId, eventId, updateRequest);
    }
}
