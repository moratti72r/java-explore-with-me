package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.constants.DateTimePattern;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventShortForComment {
    private Long id;
    private String annotation;
    private CategoryDto category;
    @JsonFormat(pattern = DateTimePattern.PATTERN)
    private LocalDateTime createdOn;
    private String description;
    @JsonFormat(pattern = DateTimePattern.PATTERN)
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private String title;

}
