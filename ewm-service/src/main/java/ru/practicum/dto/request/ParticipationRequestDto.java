package ru.practicum.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.Status;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequestDto {
    Long id;
    LocalDateTime created;
    Long event;
    Long requester;
    Status status;

}
