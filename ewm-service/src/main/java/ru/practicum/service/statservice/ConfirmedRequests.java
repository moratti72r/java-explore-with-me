package ru.practicum.service.statservice;

import lombok.Data;

@Data
public class ConfirmedRequests {

    private Long eventId;

    private Long confirmedRequestsCount;

    public ConfirmedRequests(Long eventId, Long confirmedRequestsCount) {
        this.eventId = eventId;
        this.confirmedRequestsCount = confirmedRequestsCount;
    }
}
