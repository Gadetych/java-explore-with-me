package ru.practicum.ewm.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Builder
public class EndpointHitRequestDto {
    @NotNull
    private String app;
    @NotNull
    private String uri;
    @NotNull
    private String ip;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}
