package ru.practicum.ewm.stats.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ViewStatsResponseDto {
    private String app;
    private String uri;
    private long hits;
}
