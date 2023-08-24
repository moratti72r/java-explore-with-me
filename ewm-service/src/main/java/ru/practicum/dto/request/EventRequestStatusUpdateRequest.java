package ru.practicum.dto.request;

import lombok.Data;
import ru.practicum.model.Status;

import java.util.Set;

@Data
public class EventRequestStatusUpdateRequest {
    private Set<Long> requestIds;
    private Status status;
}
