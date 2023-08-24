package ru.practicum.service.eventservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.NotMeetConditionException;
import ru.practicum.model.*;
import ru.practicum.repository.*;
import ru.practicum.statsclient.client.StatClient;
import ru.practicum.statsdto.EndpointHitDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    private final StatClient statClient;

    @Value("ewm-main-service")
    private String app;

    @Override
    public EventFullDto createEvent(long userId, NewEventDto eventDto) {

        if (eventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2)) || eventDto.getEventDate().isBefore(LocalDateTime.now())) {
            throw new NotMeetConditionException("Field: eventDate. Error: должно содержать дату, которая еще не наступила." +
                    " Value: " + eventDto.getEventDate());
        }
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

        List<EventFullDto> result = eventRepository.findAllByInitiatorId(userId, pageRequest).stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());

        log.info("Получен список событий созданных пользователем с id={}", userId);

        return result;
    }

    @Override
    public EventFullDto getEventByIdForInitiator(long userId, long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Event with id=" + userId + " was not found");
        }
        EventFullDto result = EventMapper.toEventFullDto(eventRepository.findEventByIdAndInitiatorId(eventId, userId).get());

        log.info("Получено событие с id={} от созданный пользователем с id={}", eventId, userId);

        return result;
    }

    @Override
    public EventFullDto upDateEventByInitiator(long userId, long eventId, UpdateEventUserRequest updateEvent) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Event with id=" + userId + " was not found");
        }

        Optional<Event> optEvent = eventRepository.findEventByIdAndInitiatorId(eventId, userId);
        if (optEvent.isPresent()) {
            Event event = optEvent.get();

            if (event.getState() == State.PUBLISHED) {
                throw new NotMeetConditionException("Only pending or canceled events can be changed");
            }

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

            if (updateEvent.getAnnotation() != null) {
                event.setAnnotation(updateEvent.getAnnotation());
            }
            if (updateEvent.getDescription() != null) {
                event.setDescription(updateEvent.getDescription());
            }
            if (updateEvent.getEventDate() != null) {
                if (updateEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2)) || updateEvent.getEventDate().isBefore(LocalDateTime.now())) {
                    throw new NotMeetConditionException("Field: eventDate. Error: должно содержать дату, которая еще не наступила." +
                            " Value: " + updateEvent.getEventDate());
                }
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
            if (updateEvent.getStateAction() != null) {
                if (updateEvent.getStateAction() == StateActionUser.SEND_TO_REVIEW) {
                    event.setState(State.PENDING);
                }
                if (updateEvent.getStateAction() == StateActionUser.CANCEL_REVIEW) {
                    event.setState(State.CANCELED);
                }
            }
            if (updateEvent.getTitle() != null) {
                event.setTitle(updateEvent.getTitle());
            }

            eventRepository.save(event);
            log.info("Событие с id={} обновлено", event.getId());

            return EventMapper.toEventFullDto(event);
        } else throw new NotFoundException("Собитие отсутствует");
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

        Event event = eventRepository.findById(eventId).get();

        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            throw new NotMeetConditionException("Error: Для данного события подтверждения не трубуются");
        }

        long countConfirmedStatus = request.getRequestIds().size() + requestRepository.countAllByEventIdAndStatus(eventId, Status.CONFIRMED);
        if (request.getStatus() == Status.CONFIRMED && countConfirmedStatus > event.getParticipantLimit()) {
            throw new NotMeetConditionException("Error: Количество подтвержденных заявок превышает лимит" +
                    " Value: " + countConfirmedStatus);
        }

        List<ParticipationRequest> requestList = requestRepository.findAllByIdIn(request.getRequestIds());

        boolean allExists = requestList.stream()
                .allMatch(request1 -> (request1.getEvent().getId() == eventId) && (request1.getStatus() == Status.PENDING));
        if (!allExists) {
            throw new NotMeetConditionException("Error: Все запросы должны относиться к одному событию и иметь статус PENDING");
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

        List<EventFullDto> result = eventRepository.findAllByInitiatorIdInAndStateInAndCategoryIdInAndEventDateBetween(users, states,
                        categories, rangeStart, rangeEnd, pageRequest)
                .stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());

        log.info("Получен список событий по параметрам");

        return result;
    }

    @Override
    public EventFullDto updateEventByAdmin(long eventId, UpdateEventAdminRequest updateEvent) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (!event.getEventDate().isBefore(LocalDateTime.now().minusHours(1))) {
            throw new NotMeetConditionException("Error: дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
        }

        if (event.getState() == State.PUBLISHED) {
            throw new NotMeetConditionException("Only pending or canceled events can be changed");
        }

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

        if (updateEvent.getAnnotation() != null) {
            event.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getDescription() != null) {
            event.setDescription(updateEvent.getDescription());
        }
        if (updateEvent.getEventDate() != null) {
            if (updateEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2)) || updateEvent.getEventDate().isBefore(LocalDateTime.now())) {
                throw new NotMeetConditionException("Field: eventDate. Error: должно содержать дату, которая еще не наступила." +
                        " Value: " + updateEvent.getEventDate());
            }
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
        if (updateEvent.getStateAction() != null) {
            if (updateEvent.getStateAction() == StateActionAdmin.PUBLISH_EVENT) {
                event.setState(State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            }
            if (updateEvent.getStateAction() == StateActionAdmin.REJECT_EVENT) {
                event.setState(State.CANCELED);
            }
        }
        if (updateEvent.getTitle() != null) {
            event.setTitle(updateEvent.getTitle());
        }

        eventRepository.save(event);
        log.info("Событие с id={} обновлено", event.getId());

        return EventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getEventsByParameterForUser(String text, List<Long> categories, Boolean paid,
                                                           LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                           Boolean onlyAvailable, String sort, Integer from,
                                                           Integer size, HttpServletRequest request) {

        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Event> events;
        if (sort == null) {
            sort = "id";
        }
        switch (sort) {
            case "id":
                events = eventRepository.findAllByParametersSortedById(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, pageRequest);
                break;
            case "EVENT_DATE":
                events = eventRepository.findAllByParametersSortedByDate(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, pageRequest);
                break;
            case "VIEWS":
                events = eventRepository.findAllByParametersSortedByViews(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, pageRequest);
                break;
            default:
                throw new NotMeetConditionException("Error: Не правильно заданный параметр сортировки");
        }

        List<EventShortDto> eventDtoList = new ArrayList<>();
        if (!events.isEmpty()) {
            eventDtoList = events.stream()
                    .map(EventMapper::toEventShortDto)
                    .collect(Collectors.toList());

            events.forEach(event -> event.setView(event.getView() + 1));
            eventRepository.saveAll(events);
        }

        statClient.addPost(EndpointHitDto.builder()
                .app(app)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());

        log.info("Получен список событий");
        return eventDtoList;


    }

    @Override
    public EventFullDto getEventByIdForUser(long id, HttpServletRequest request) {
        Event event = eventRepository.findEventByIdAndState(id, State.PUBLISHED).orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));
        event.setView(event.getView() + 1);
        eventRepository.save(event);

        statClient.addPost(EndpointHitDto.builder()
                .app(app)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());

        log.info("Получено событие с id={}", id);
        return EventMapper.toEventFullDto(event);
    }
}
