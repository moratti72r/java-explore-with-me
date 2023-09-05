package ru.practicum.service.eventservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.dto.event.*;
import ru.practicum.dto.mapper.EventMapper;
import ru.practicum.dto.mapper.LocationMapper;
import ru.practicum.dto.mapper.ParticipationRequestMapper;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.exception.MethodArgumentNotMeetLogicAppException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.NotMeetConditionException;
import ru.practicum.exception.NotMeetLogicAppException;
import ru.practicum.model.*;
import ru.practicum.repository.*;
import ru.practicum.service.statservice.StatService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;

    private final LocationRepository locationRepository;

    private final RequestRepository requestRepository;

    private final StatService statService;

    @Override
    public EventFullDto createEvent(long userId, NewEventDto eventDto) {

        Category category = categoryRepository.findById(eventDto.getCategory())
                .orElseThrow(() -> new NotMeetConditionException("Должен содержать категорию которая существует. " +
                        "Value: " + eventDto.getCategory()));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Location location = locationRepository.save(LocationMapper.toLocation(eventDto.getLocation()));

        Event saveEvent = EventMapper.toEvent(eventDto);
        saveEvent.setCategory(category);
        saveEvent.setInitiator(user);
        saveEvent.setLocation(location);
        saveEvent.setState(State.PENDING);
        saveEvent.setCreatedOn(LocalDateTime.now());

        Event result = eventRepository.save(saveEvent);
        log.info("Событие с id={} от Пользователя с id={} добавлено", result.getId(), userId);

        return EventMapper.toEventFullDto(result);
    }

    @Override
    public List<EventFullDto> getAllEventsForInitiator(long userId, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by("id").ascending());

        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageRequest);

        List<EventFullDto> result = getEventFullDtoList(events);

        log.info("Получен список событий созданных пользователем с id={}", userId);
        return result;
    }

    @Override
    public EventFullDto getEventByIdForInitiator(long userId, long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        Event event = eventRepository.findEventByIdAndInitiatorId(eventId, userId).orElseThrow(() -> new NotFoundException("Event with id=" + userId + " was not found"));

        EventFullDto result = EventMapper.toEventFullDto(event);
        result.setConfirmedRequests(requestRepository.countAllByEventIdAndStatus(eventId, Status.CONFIRMED));
        result.setViews(statService.getViews(List.of(event)).getOrDefault(eventId, 0L));

        log.info("Получено событие с id={} от созданный пользователем с id={}", eventId, userId);

        return result;
    }

    @Override
    public EventFullDto upDateEventByInitiator(long userId, long eventId, UpdateEventUserRequest updateEvent) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        Event event = eventRepository.findEventByIdAndInitiatorId(eventId, userId).orElseThrow(() -> new NotMeetLogicAppException("Событие с таким создателем отсутствует"));

        if (event.getState() == State.PUBLISHED) {
            throw new NotMeetLogicAppException("Данное поле не требует изменения");
        }

        updateEvent(event, updateEvent);

        if (updateEvent.getStateAction() != null) {
            if (updateEvent.getStateAction() == UpdateEventUserRequest.StateAction.SEND_TO_REVIEW) {
                event.setState(State.PENDING);
            }
            if (updateEvent.getStateAction() == UpdateEventUserRequest.StateAction.CANCEL_REVIEW) {
                event.setState(State.CANCELED);
            }
        }

        eventRepository.save(event);
        log.info("Событие с id={} обновлено", event.getId());

        EventFullDto result = EventMapper.toEventFullDto(event);
        result.setConfirmedRequests(requestRepository.countAllByEventIdAndStatus(eventId, Status.CONFIRMED));
        result.setViews(statService.getViews(List.of(event)).getOrDefault(eventId, 0L));

        return result;

    }

    @Override
    public List<ParticipationRequestDto> getRequestByInitiatorAndEvent(long userId, long eventId) {
        if (!eventRepository.existsByIdAndInitiatorId(eventId, userId)) {
            throw new NotMeetConditionException("Field: eventDate. Error: Пользователь должен являться инициатором события" +
                    " Value: " + userId);
        }
        List<ParticipationRequestDto> result = requestRepository.findAllByEventId(eventId).stream()
                .map(ParticipationRequestMapper::toRequestDto)
                .collect(Collectors.toList());
        log.info("Получен список запросов на участие в событии с id={} с создателем с id={}", userId, eventId);
        return result;
    }

    @Override
    public EventRequestStatusUpdateResult upDateStatusRequestsByInitiator(long userId, long eventId, EventRequestStatusUpdateRequest request) {
        if (!eventRepository.existsByIdAndInitiatorId(eventId, userId)) {
            throw new NotMeetConditionException("Error: Пользователь должен являться инициатором события" +
                    " Value: " + userId);
        }

        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            throw new NotMeetConditionException("Error: Для данного события подтверждения не требуются");
        }

        long countConfirmedStatus = request.getRequestIds().size() + requestRepository.countAllByEventIdAndStatus(eventId, Status.CONFIRMED);
        if (request.getStatus() == Status.CONFIRMED && countConfirmedStatus > event.getParticipantLimit()) {
            throw new NotMeetLogicAppException("Error: Количество подтвержденных заявок превышает лимит" +
                    " Value: " + countConfirmedStatus);
        }

        List<ParticipationRequest> requestList = requestRepository.findAllByIdIn(request.getRequestIds());

        boolean allExists = requestList.stream()
                .allMatch(request1 -> (request1.getEvent().getId() == eventId) && (request1.getStatus() == Status.PENDING));
        if (!allExists) {
            throw new NotMeetLogicAppException("Error: Все запросы должны относиться к одному событию и иметь статус PENDING");
        }

        requestList.forEach(request1 -> request1.setStatus(request.getStatus()));
        requestRepository.saveAll(requestList);

        if (requestRepository.countAllByEventIdAndStatus(eventId, Status.CONFIRMED) == event.getParticipantLimit()) {
            List<ParticipationRequest> listRequest = requestRepository.findAllByEventIdAndStatus(eventId, Status.PENDING);
            listRequest.forEach(request1 -> request1.setStatus(Status.REJECTED));
            requestRepository.saveAll(listRequest);
        }

        List<ParticipationRequestDto> confirmedList = requestRepository.findAllByEventIdAndStatus(eventId, Status.CONFIRMED).stream()
                .map(ParticipationRequestMapper::toRequestDto)
                .collect(Collectors.toList());

        List<ParticipationRequestDto> rejectedList = requestRepository.findAllByEventIdAndStatus(eventId, Status.REJECTED).stream()
                .map(ParticipationRequestMapper::toRequestDto)
                .collect(Collectors.toList());

        EventRequestStatusUpdateResult result = EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedList)
                .rejectedRequests(rejectedList)
                .build();

        log.info("Статус запросов от пользователей {} на событие c id={} изменен на {} создателем события с id={}",
                request.getRequestIds(), eventId, request.getStatus(), userId);
        return result;
    }

    @Override
    public List<EventFullDto> getAllEventsForAdmin(List<Long> users, List<State> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by("id").ascending());

        List<Event> events = eventRepository.findAllByParameters(users, states,
                categories, rangeStart, rangeEnd, pageRequest);

        List<EventFullDto> result = getEventFullDtoList(events);

        log.info("Получен список событий по параметрам");
        return result;
    }

    @Override
    public EventFullDto updateEventByAdmin(long eventId, UpdateEventAdminRequest updateEvent) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (event.getState() != State.PENDING) {
            throw new NotMeetLogicAppException("Данное поле не требует изменения");
        }

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new NotMeetConditionException("Error: дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
        }

        updateEvent(event, updateEvent);

        if (updateEvent.getStateAction() != null) {
            if (updateEvent.getStateAction() == UpdateEventAdminRequest.StateAction.PUBLISH_EVENT) {
                event.setState(State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            }
            if (updateEvent.getStateAction() == UpdateEventAdminRequest.StateAction.REJECT_EVENT) {
                event.setState(State.CANCELED);
            }
        }

        eventRepository.save(event);
        log.info("Событие с id={} обновлено", event.getId());

        EventFullDto result = EventMapper.toEventFullDto(event);
        result.setConfirmedRequests(requestRepository.countAllByEventIdAndStatus(eventId, Status.CONFIRMED));
        result.setViews(statService.getViews(List.of(event)).getOrDefault(eventId, 0L));

        return result;
    }

    @Override
    public List<EventShortDto> getEventsByParameterForUser(String text, List<Long> categories, Boolean paid,
                                                           LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                           Boolean onlyAvailable, String sort, int from,
                                                           int size, HttpServletRequest request) {

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new MethodArgumentNotMeetLogicAppException("Начало события не может быть позже конца события");
        }
        if (rangeStart == null && rangeEnd == null) {
            rangeStart = LocalDateTime.now();
        }

        if (sort != null && sort.equals("EVENT_DATE")) {
            sort = "eventDate";
        } else {
            sort = "id";
        }

        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(sort).descending());
        List<Event> events = eventRepository.findAllByParametersForUser(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, State.PUBLISHED, pageRequest);

        List<EventShortDto> eventDtoList = new ArrayList<>();
        if (!events.isEmpty()) {

            Map<Long, Long> confirmedRequest = statService.getConfirmedRequests(events);

            Map<Long, Long> views = statService.getViews(events);

            eventDtoList = events.stream()
                    .map(EventMapper::toEventShortDto)
                    .collect(Collectors.toList());
            eventDtoList.forEach(eventShortDto -> {
                eventShortDto.setConfirmedRequests(confirmedRequest.getOrDefault(eventShortDto.getId(), 0L));
                eventShortDto.setViews(views.getOrDefault(eventShortDto.getId(), 0L));
            });
        }

        if (sort.equals("VIEWS")) {
            eventDtoList.sort(Comparator.comparing(EventShortDto::getViews));
        }

        statService.addHits(request);

        log.info("Получен список событий");
        return eventDtoList;
    }

    @Override
    public EventFullDto getEventByIdForUser(long id, HttpServletRequest request) {
        Event event = eventRepository.findEventByIdAndState(id, State.PUBLISHED).orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));

        statService.addHits(request);

        log.info("Получено событие с id={}", id);

        EventFullDto result = EventMapper.toEventFullDto(event);
        result.setConfirmedRequests(requestRepository.countAllByEventIdAndStatus(id, Status.CONFIRMED));
        result.setViews(statService.getViews(List.of(event)).getOrDefault(id, 0L));

        return result;
    }

    private List<EventFullDto> getEventFullDtoList(List<Event> events) {
        if (!events.isEmpty()) {

            Map<Long, Long> confirmedRequest = statService.getConfirmedRequests(events);

            Map<Long, Long> views = statService.getViews(events);

            List<EventFullDto> result = events
                    .stream()
                    .map(EventMapper::toEventFullDto)
                    .collect(Collectors.toList());
            result.forEach(eventFullDto -> {
                eventFullDto.setConfirmedRequests(confirmedRequest.getOrDefault(eventFullDto.getId(), 0L));
                eventFullDto.setViews(views.getOrDefault(eventFullDto.getId(), 0L));
            });
            return result;
        } else return List.of();
    }

    private void updateEvent(Event event, UpdateEventRequest updateEvent) {

        if (updateEvent.getCategory() != null) {
            Category category = categoryRepository.findById(updateEvent.getCategory()).orElseThrow(
                    () -> new NotMeetConditionException("Field: category. Error: должно содержать категорию, которая существует." +
                            " Value: " + updateEvent.getCategory()));

            event.setCategory(category);
        }

        if (updateEvent.getLocation() != null) {
            Location location = locationRepository.findByLatAndLon(updateEvent.getLocation().getLat(),
                            updateEvent.getLocation().getLat())
                    .orElse(locationRepository.save(LocationMapper.toLocation(updateEvent.getLocation())));

            event.setLocation(location);
        }

        if (updateEvent.getAnnotation() != null && !updateEvent.getAnnotation().isBlank()) {
            event.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getDescription() != null && !updateEvent.getDescription().isBlank()) {
            event.setDescription(updateEvent.getDescription());
        }
        if (updateEvent.getEventDate() != null) {
            event.setEventDate(updateEvent.getEventDate());
        }
        if (updateEvent.getPaid() != null) {
            event.setPaid(updateEvent.getPaid());
        }
        if (updateEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEvent.getParticipantLimit());
        }
        if (updateEvent.getRequestModeration() != null) {
            event.setRequestModeration(updateEvent.getRequestModeration());
        }
        if (updateEvent.getTitle() != null && !updateEvent.getTitle().isBlank()) {
            event.setTitle(updateEvent.getTitle());
        }
    }
}
