package ru.practicum.dto.compilation;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewCompilationDto {
    @Builder.Default
    private List<Long> events = List.of();
    private boolean pinned;
    @NotBlank
    @Length(min = 1, max = 50)
    private String title;
}
