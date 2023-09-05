package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.constants.DateTimePattern;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.State;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {
    private Long id;
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;
    @JsonFormat(pattern = DateTimePattern.PATTERN)
    private LocalDateTime createdOn;
    private String description;
    @JsonFormat(pattern = DateTimePattern.PATTERN)
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private LocationDto location;
    private boolean paid;
    private int participantLimit;
    @JsonFormat(pattern = DateTimePattern.PATTERN)
    private LocalDateTime publishedOn;
    private boolean requestModeration = true;
    private State state;
    private String title;
    private Long views;
}
