package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.constants.DateTimePattern;
import ru.practicum.dto.event.validator.TwoHoursAhead;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventRequest {
    @Size(min = 20, max = 2000)
    protected String annotation;

    protected Long category;

    @Size(min = 20, max = 7000)
    protected String description;

    @TwoHoursAhead
    @JsonFormat(pattern = DateTimePattern.PATTERN)
    protected LocalDateTime eventDate;

    @Valid
    protected LocationDto location;

    protected Boolean paid;

    @PositiveOrZero
    protected Integer participantLimit;

    protected Boolean requestModeration;

    @Size(min = 3, max = 120)
    protected String title;
}
