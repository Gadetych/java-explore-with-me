package ru.practicum.controller.privy;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.service.EventsService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateEventsController {
    private final EventsService service;

    @GetMapping
    public List<EventShortDto> findAll(@PathVariable long userId,
                                       @RequestParam(required = false, defaultValue = "0") int from,
                                       @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("==> Find all events by userId {}, from {}, size {}", userId, from, size);
        return service.findAll(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable long userId,
                               @RequestBody @Valid NewEventDto requestBody) {
        log.info("==> Create new event {} by userId {}", requestBody, userId);
        return service.create(userId, requestBody);
    }

    @GetMapping("/{eventId}")
    public EventFullDto findById(@PathVariable long userId,
                                 @PathVariable long eventId) {
        log.info("==> Find event {} by userId {}", eventId, userId);
        return service.findById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable long userId,
                               @PathVariable long eventId,
                               @RequestBody @Valid UpdateEventUserRequest requestBody) {
        log.info("==> Update event {} by userId {}, request body: {}", eventId, userId, requestBody);
        return service.update(userId, eventId, requestBody);
    }

    //    TODO: Доработать тесты для этих методов
    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> findRequests(@PathVariable long userId,
                                                      @PathVariable long eventId) {
//        TODO: В случае, если по заданным фильтрам не найдено ни одной заявки, возвращает пустой список
        log.info("==> Find requests by userId {}, event id {}", userId, eventId);
        return service.findRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateStatusRequest(@PathVariable long userId,
                                                       @PathVariable long eventId,
                                                       @RequestBody @Valid EventRequestStatusUpdateRequest requestBody) {
        log.info("==> Update status request {} by userId {}, event id {}", requestBody, userId, eventId);
//    TODO: если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
//нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
//статус можно изменить только у заявок, находящихся в состоянии ожидания (Ожидается код ошибки 409)
//если при подтверждении данной заявки, лимит заявок для события исчерпан, то все неподтверждённые заявки необходимо отклонить
        return service.updateStatusRequest(userId, eventId, requestBody);
    }
}
