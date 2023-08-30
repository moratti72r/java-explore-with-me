package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.constants.DateTimePattern;
import ru.practicum.dto.event.validator.TwoHoursAhead;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull
    private Long category;

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull
    @TwoHoursAhead
    @JsonFormat(pattern = DateTimePattern.PATTERN)
    private LocalDateTime eventDate;

    @Valid
    @NotNull
    private LocationDto location;

    private boolean paid;

    @PositiveOrZero
    private int participantLimit;

    @NotNull
    private Boolean requestModeration = true;

    @NotBlank
    @Size(min = 3, max = 120)
    private String title;
}
