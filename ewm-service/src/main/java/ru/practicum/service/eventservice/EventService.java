package ru.practicum.service.eventservice;

import ru.practicum.dto.event.*;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.model.State;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto createEvent(long userId, NewEventDto eventDto);

    List<EventFullDto> getAllEventsForInitiator(long userId, int from, int size);

    EventFullDto getEventByIdForInitiator(long userId, long eventId);

    EventFullDto upDateEventByInitiator(long userId, long eventId, UpdateEventUserRequest updateEvent);

    List<ParticipationRequestDto> getRequestByInitiatorAndEvent(long userId, long eventId);

    EventRequestStatusUpdateResult upDateStatusRequestsByInitiator(long userId, long eventId, EventRequestStatusUpdateRequest request);

    List<EventFullDto> getAllEventsForAdmin(List<Long> users, List<State> states, List<Long> categories,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    EventFullDto updateEventByAdmin(long eventId, UpdateEventAdminRequest event);

    List<EventShortDto> getEventsByParameterForUser(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                                    LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from,
                                                    Integer size, HttpServletRequest request);

    EventFullDto getEventByIdForUser(long id, HttpServletRequest request);

    //добавить методы для запросов
}
