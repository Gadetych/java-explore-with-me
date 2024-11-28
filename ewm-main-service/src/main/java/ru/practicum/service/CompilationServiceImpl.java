package ru.practicum.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.PublicCompilationParam;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.exception.not_found.NotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.model.QCompilation;
import ru.practicum.repository.CompilationRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventsService eventsService;

    //    Admin
    @EntityGraph(attributePaths = {"events"})
    @Transactional
    @Override
    public CompilationDto create(NewCompilationDto requestBody) {
        log.debug("==> Admin create compilation, requestBody: {}", requestBody);
        List<EventFullDto> eventFullDtoList;
        if (requestBody.getEvents() != null && !requestBody.getEvents().isEmpty()) {
            eventFullDtoList = eventsService.findAllById(requestBody.getEvents());
        } else eventFullDtoList = List.of();
        Compilation model = CompilationMapper.newDtoToModel(requestBody, eventFullDtoList);
        model = compilationRepository.save(model);
        CompilationDto result = CompilationMapper.modelToDto(model, eventFullDtoList);
        log.debug("<== Admin created compilation, result: {}", result);
        return result;
    }

    @Override
    public void delete(long compId) {
        log.debug("==> Admin delete compilation, compId: {}", compId);
        compilationRepository.deleteById(compId);
    }

    @EntityGraph(attributePaths = {"events"})
    @Transactional
    @Override
    public CompilationDto update(long compId, UpdateCompilationRequest requestBody) {
        log.debug("==> Admin update compilation, compId: {}, request body: {}", compId, requestBody);
        Compilation model = compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException("Compilation not found with id: " + compId));
        List<EventFullDto> eventFullDtoList = eventsService.findAllById(requestBody.getEvents());
        model = compilationRepository.save(updateCompilationWithNewParam(model, eventFullDtoList, requestBody));
        CompilationDto result = CompilationMapper.modelToDto(model, eventFullDtoList);
        log.debug("<== Admin updated compilation, result: {}", result);
        return result;
    }

    private Compilation updateCompilationWithNewParam(Compilation model, List<EventFullDto> eventFullDtoList, UpdateCompilationRequest requestBody) {
        if (requestBody.getEvents() != null && !requestBody.getEvents().isEmpty()) {
            model.setEvents(eventFullDtoList.stream().map(EventMapper::fullDtoToModel).collect(Collectors.toList()));
        }
        if (requestBody.getTitle() != null) {
            model.setTitle(requestBody.getTitle());
        }
        if (requestBody.getPinned() != null) {
            model.setPinned(requestBody.getPinned());
        }
        return model;
    }

    //    Public
    @EntityGraph(attributePaths = {"events"})
    @Override
    public List<CompilationDto> findAll(PublicCompilationParam paramSearch) {
        log.debug("==> Public find all compilations, paramSearch: {}", paramSearch);
        BooleanExpression predicate = selectPredicate(paramSearch);
        List<Compilation> compilations = getCompilations(predicate, paramSearch.getFrom(), paramSearch.getSize());
        Map<Long, List<EventFullDto>> mapCompIdByListEventFull = getEventsFullDto(compilations);
        List<CompilationDto> result = getCompilationDtoList(compilations, mapCompIdByListEventFull);
        log.debug("<== Public found all events result {}", result);
        return result;
    }

    private BooleanExpression selectPredicate(PublicCompilationParam paramSearch) {
        BooleanExpression predicate = QCompilation.compilation.isNotNull();
        if (paramSearch.getPinned() != null) {
            predicate = predicate.and(QCompilation.compilation.pinned.eq(paramSearch.getPinned()));
        }
        return predicate;
    }

    private List<Compilation> getCompilations(BooleanExpression predicate, int from, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageRequest = PageRequest.of(from / size, size, sort);
        return compilationRepository.findAll(predicate, pageRequest).toList();
    }

    private Map<Long, List<EventFullDto>> getEventsFullDto(List<Compilation> compilations) {
        List<Long> eventsIds = compilations.stream()
                .flatMap(compilation -> compilation.getEvents().stream().map(Event::getId))
                .distinct()
                .toList();
        List<EventFullDto> eventFullDtoList = eventsService.findAllById(eventsIds);

        Map<Long, EventFullDto> mapEventIdByEventFull = new HashMap<>();
        for (EventFullDto eventFullDto : eventFullDtoList) {
            mapEventIdByEventFull.put(eventFullDto.getId(), eventFullDto);
        }

        Map<Long, List<EventFullDto>> result = new HashMap<>();
        for (Compilation compilation : compilations) {
            List<EventFullDto> listEventFullDto = new ArrayList<>();
            for (Event event : compilation.getEvents()) {
                listEventFullDto.add(mapEventIdByEventFull.get(event.getId()));
            }
            result.put(compilation.getId(), listEventFullDto);
        }

        return result;
    }

    private List<CompilationDto> getCompilationDtoList(List<Compilation> compilations, Map<Long, List<EventFullDto>> mapCompIdByListEventFull) {
        return compilations.stream()
                .map(compilation -> CompilationMapper.modelToDto(compilation, mapCompIdByListEventFull.get(compilation.getId())))
                .toList();
    }

    @Override
    public CompilationDto findById(long compId) {
        log.debug("==> Admin find compilation, compId: {}", compId);
        Compilation model = compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException("Compilation not found with id: " + compId));
        List<EventFullDto> eventFullDtoList = eventsService.findAllById(
                model.getEvents().stream()
                        .map(Event::getId)
                        .toList()
        );
        CompilationDto result = CompilationMapper.modelToDto(model, eventFullDtoList);
        log.debug("<== Admin found compilation, result: {}", result);
        return result;
    }
}
