package ru.practicum.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.model.ParticipationRequest;

@UtilityClass
public class ParticipationRequestMapper {
    public ParticipationRequestDto toRequestDto(ParticipationRequest request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
    }
}
