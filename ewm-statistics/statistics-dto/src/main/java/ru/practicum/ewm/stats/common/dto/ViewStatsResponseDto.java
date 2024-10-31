package ru.practicum.ewm.stats.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewStatsResponseDto {
    private String app;
    private String uri;
    private long hits;
}
