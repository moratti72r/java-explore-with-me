package ru.practicum.statsdto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class EndpointHitDto {

    private Long id;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}
