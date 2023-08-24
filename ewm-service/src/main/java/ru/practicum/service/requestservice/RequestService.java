package ru.practicum.service.requestservice;

import ru.practicum.dto.request.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto createRequest(long userId, long eventId);

    List<ParticipationRequestDto> getRequestByIdUser(long userId);

    ParticipationRequestDto upDateStatusOnCanceled(long userId, long requestId);
}
