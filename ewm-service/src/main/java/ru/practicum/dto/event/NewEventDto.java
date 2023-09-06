package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.constants.DateTimePattern;
import ru.practicum.dto.event.validator.TwoHoursAhead;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {

    @NotBlank
    @Size(min = 20, max = 2000)
    String annotation;

    @NotNull
    Long category;

    @NotBlank
    @Size(min = 20, max = 7000)
    String description;

    @NotNull
    @TwoHoursAhead
    @JsonFormat(pattern = DateTimePattern.PATTERN)
    LocalDateTime eventDate;

    @Valid
    @NotNull
    LocationDto location;

    boolean paid;

    @PositiveOrZero
    int participantLimit;

    @NotNull
    Boolean requestModeration = true;

    @NotBlank
    @Size(min = 3, max = 120)
    String title;
}
