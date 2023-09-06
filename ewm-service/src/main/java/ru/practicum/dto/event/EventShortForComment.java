package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.constants.DateTimePattern;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortForComment {
    Long id;
    String annotation;
    CategoryDto category;
    @JsonFormat(pattern = DateTimePattern.PATTERN)
    LocalDateTime createdOn;
    String description;
    @JsonFormat(pattern = DateTimePattern.PATTERN)
    LocalDateTime eventDate;
    UserShortDto initiator;
    String title;

}
