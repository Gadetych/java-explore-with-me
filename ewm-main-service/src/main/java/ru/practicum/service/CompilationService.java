package ru.practicum.service;

import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;

public interface CompilationService {
    CompilationDto create(NewCompilationDto requestBody);

    void delete(long compId);

    CompilationDto update(long compId, UpdateCompilationRequest requestBody);
}
