package ru.practicum.service.statservice;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfirmedRequests {

    Long eventId;

    Long confirmedRequestsCount;

    public ConfirmedRequests(Long eventId, Long confirmedRequestsCount) {
        this.eventId = eventId;
        this.confirmedRequestsCount = confirmedRequestsCount;
    }
}
