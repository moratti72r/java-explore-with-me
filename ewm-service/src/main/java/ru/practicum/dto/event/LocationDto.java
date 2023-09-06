package ru.practicum.dto.event;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocationDto {

    @Max(90)
    @Min(-90)
    double lat;

    @Max(180)
    @Min(-180)
    double lon;
}
