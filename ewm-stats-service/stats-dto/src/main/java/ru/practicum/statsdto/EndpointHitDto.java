package ru.practicum.statsdto;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data

public class EndpointHitDto {

    private long id;
    @NotNull
    @Size(max = 32)
    private String app;
    @NotNull
    @Size(max = 128)
    private String uri;
    @NotNull
    @Size(max = 16)
    private String ip;
    @NotNull
    private LocalDateTime timestamp;
}
