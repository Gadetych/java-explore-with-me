package ru.practicum.controller.open;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.PublicParamEvent;
import ru.practicum.enums.SortEvent;
import ru.practicum.ewm.stats.client.StatClient;
import ru.practicum.ewm.stats.common.dto.EndpointHitRequestDto;
import ru.practicum.exception.validation.ConstraintViolationParameterSearchException;
import ru.practicum.service.EventsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PublicEventsController {
    private final EventsService service;
    private final StatClient client;
    private final String app = "ewm-main-service";

    @GetMapping
    public List<EventShortDto> findAll(@RequestParam(value = "text", required = false) String text,
                                       @RequestParam(value = "categories", required = false) List<Long> categories,
                                       @RequestParam(value = "paid", required = false) Boolean paid,
                                       @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                       @RequestParam(value = "rangeStart", required = false) LocalDateTime rangeStart,
                                       @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                       @RequestParam(value = "rangeEnd", required = false) LocalDateTime rangeEnd,
                                       @RequestParam(value = "onlyAvailable", required = false, defaultValue = "false") Boolean onlyAvailable,
                                       @RequestParam(value = "sort", required = false) SortEvent sort,
                                       @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                       @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
                                       HttpServletRequest httpServletRequest) throws ConstraintViolationException {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ConstraintViolationParameterSearchException("rangeStart must be before rangeEnd");
        }
        PublicParamEvent paramSearch = PublicParamEvent.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .sort(sort)
                .from(from)
                .size(size)
                .build();
        log.info("==> Find all events with parameters: {}", paramSearch);
        EndpointHitRequestDto endpointHitRequestDto = EndpointHitRequestDto.builder()
                .app(app)
                .ip(httpServletRequest.getRemoteAddr())
                .uri(httpServletRequest.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        client.save(endpointHitRequestDto);
        return service.findAll(paramSearch);
    }

    @GetMapping("/{id}")
    public EventFullDto findById(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        log.info("==> Find event by id: {}", id);
        EndpointHitRequestDto endpointHitRequestDto = EndpointHitRequestDto.builder()
                .app(app)
                .ip(httpServletRequest.getRemoteAddr())
                .uri(httpServletRequest.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        client.save(endpointHitRequestDto);
        return service.findById(id);
    }
}
