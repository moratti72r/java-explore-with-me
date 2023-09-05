package ru.practicum.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {

    @Max(90)
    @Min(-90)
    private double lat;

    @Max(180)
    @Min(-180)
    private double lon;
}
