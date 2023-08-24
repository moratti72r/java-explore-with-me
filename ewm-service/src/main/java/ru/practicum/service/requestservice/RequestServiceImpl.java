package ru.practicum.service.requestservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.mapper.ParticipationRequestMapper;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.NotMeetConditionException;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.Status;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    @Override
    public ParticipationRequestDto createRequest(long userId, long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (event.getInitiator().getId() == userId) {
            throw new NotMeetConditionException("Error: Создатель события не должен подавать заявку" +
                    " Value: " + userId);
        }
        if (requestRepository.countAllByEventIdAndStatus(eventId, Status.CONFIRMED) == event.getParticipantLimit()) {
            throw new NotMeetConditionException("Error: Количество заявок не должен превышать лимит" +
                    " Value: " + event.getParticipantLimit());
        }
        Status requestStatus;
        if (!event.isRequestModeration()) {
            requestStatus = Status.CONFIRMED;
        } else {
            requestStatus = Status.PENDING;
        }

        ParticipationRequest request = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .status(requestStatus)
                .build();

        ParticipationRequest result = requestRepository.save(request);
        if (result.getStatus() == Status.CONFIRMED) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }
        log.info("Создан запрос на участие с id={} от пользователя с id={}", result.getId(), userId);
        return ParticipationRequestMapper.toRequestDto(result);
    }

    @Override
    public List<ParticipationRequestDto> getRequestByIdUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        List<ParticipationRequestDto> result = requestRepository.findAllByRequesterId(userId).stream()
                .map(ParticipationRequestMapper::toRequestDto)
                .collect(Collectors.toList());

        log.info("Получены запросы на участие созданные пользователем с id={}", userId);

        return result;
    }

    @Override
    public ParticipationRequestDto upDateStatusOnCanceled(long userId, long requestId) {
        if (!requestRepository.existsByIdAndRequesterId(requestId, userId)) {
            throw new NotFoundException("Request with id=" + requestId + " and requesterId=" + userId + " was not found");
        }
        ParticipationRequest request = requestRepository.findById(requestId).get();

        Status status = request.getStatus();
        Event event = request.getEvent();

        request.setStatus(Status.CANCELED);
        requestRepository.save(request);

        if (status == Status.CONFIRMED) {
            event.setConfirmedRequests(event.getConfirmedRequests() - 1);
            eventRepository.save(event);
        }

        log.info("Статус запроса на участие с id={} изменен на {} создателем запроса с id={}", requestId, Status.CANCELED, userId);
        return ParticipationRequestMapper.toRequestDto(request);
    }
}
