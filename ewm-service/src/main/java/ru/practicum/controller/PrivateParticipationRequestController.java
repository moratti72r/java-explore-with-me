package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.service.requestservice.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/users")
public class PrivateParticipationRequestController {
    private final RequestService requestService;

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable long userId,
                                                 @RequestParam(name = "eventId") long eventId) {
        log.info("Получен POST запрос /users/{}/requests?eventId={}", userId, eventId);
        return requestService.createRequest(userId, eventId);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getRequests(@PathVariable long userId) {
        log.info("Получен GET запрос /users/{}/requests", userId);
        return requestService.getRequestByIdUser(userId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto changeStatusOnCancel(@PathVariable long userId,
                                                        @PathVariable long requestId) {
        log.info("Получен PATCH запрос /users/{}/requests/{}/cancel", userId, requestId);
        return requestService.upDateStatusOnCanceled(userId, requestId);
    }
}
