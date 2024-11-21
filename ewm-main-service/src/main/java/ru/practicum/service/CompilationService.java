package ru.practicum.service;

import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.PublicCompilationParam;
import ru.practicum.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto create(NewCompilationDto requestBody);

    void delete(long compId);

    CompilationDto update(long compId, UpdateCompilationRequest requestBody);

    List<CompilationDto> findAll(PublicCompilationParam build);

    CompilationDto findById(long compId);
}
