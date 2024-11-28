package ru.practicum.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PublicCompilationParam {
    Boolean pinned;
    @Builder.Default
    Integer from = 0;
    @Builder.Default
    Integer size = 10;
}
