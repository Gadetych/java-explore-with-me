package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.enums.CompilationMapper;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Compilation;
import ru.practicum.repository.CompilationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventsService eventsService;

    //    Admin
    @Transactional
    @Override
    public CompilationDto create(NewCompilationDto requestBody) {
        log.debug("==> Admin create compilation, requestBody: {}", requestBody);
        List<EventFullDto> eventFullDtoList = eventsService.findAllById(requestBody.getEvents());
        Compilation model = CompilationMapper.newDtoToModel(requestBody, eventFullDtoList);
        model = compilationRepository.save(model);
        CompilationDto result = CompilationMapper.modelToDto(model, eventFullDtoList);
        log.debug("<== Admin create compilation, result: {}", result);
        return result;
    }

    @Override
    public void delete(long compId) {
        log.debug("==> Admin delete compilation, compId: {}", compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto update(long compId, UpdateCompilationRequest requestBody) {
        log.debug("==> Admin update compilation, compId: {}, request body: {}", compId, requestBody);
        List<EventFullDto> eventFullDtoList = eventsService.findAllById(requestBody.getEvents());
        if (eventFullDtoList.isEmpty()) {
            throw new NotFoundException("No found event with ids: " + requestBody.getEvents());
        }
        Compilation model = compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException("Compilation not found with id: " + compId));
//       TODO заменит или добавит список??
        model = compilationRepository.save(updateCompilationWithNewParam(model, eventFullDtoList, requestBody));
        CompilationDto result = CompilationMapper.modelToDto(model, eventFullDtoList);
        log.debug("<== Admin update compilation, result: {}", result);
        return result;
    }

    private Compilation updateCompilationWithNewParam(Compilation model, List<EventFullDto> eventFullDtoList, UpdateCompilationRequest requestBody) {
        if (requestBody.getEvents() != null && !requestBody.getEvents().isEmpty()) {
            model.setEvents(eventFullDtoList.stream().map(EventMapper::fullDtoToModel).toList());
        }
        if (requestBody.getTitle() != null) {
            model.setTitle(requestBody.getTitle());
        }
        if (requestBody.getPinned() != null) {
            model.setPinned(requestBody.getPinned());
        }
        return model;
    }
}
