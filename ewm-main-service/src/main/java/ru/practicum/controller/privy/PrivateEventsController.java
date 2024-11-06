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
import ru.practicum.service.PrivateEventsService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateEventsController {
    private final PrivateEventsService service;

    @GetMapping
    public List<EventShortDto> findAll(@PathVariable long userId,
                                       @RequestParam(required = false, defaultValue = "0") int from,
                                       @RequestParam(required = false, defaultValue = "10") int size) {
        return service.findAll(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable long userId,
                               @RequestBody @Valid NewEventDto requestBody) {
        return service.create(userId, requestBody);
    }

    @GetMapping("/{eventId}")
    public EventFullDto findById(@PathVariable long userId,
                                 @PathVariable long eventId) {
//       TODO: В случае, если события с заданным id не найдено, возвращает статус код 404
        return service.findById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable long userId,
                               @PathVariable long eventId,
                               @RequestBody @Valid UpdateEventUserRequest requestBody) {
//  TODO:  изменить можно только отмененные события или события в состоянии ожидания модерации (Ожидается код ошибки 409)
//дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента (Ожидается код ошибки 409)
//Данные для изменения информации о событии. Если поле в запросе не указано (равно null) - значит изменение этих данных не треубется.
        return service.update(userId, eventId, requestBody);
    }

    //    TODO: Доработать тесты для этих методов
    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> findRequests(@PathVariable long userId,
                                                      @PathVariable long eventId) {
//        TODO: В случае, если по заданным фильтрам не найдено ни одной заявки, возвращает пустой список
        return null;
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateResult(@PathVariable long userId,
                                                       @PathVariable long eventId,
                                                       @RequestBody @Valid EventRequestStatusUpdateRequest requestBody) {
//    TODO: если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
//нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
//статус можно изменить только у заявок, находящихся в состоянии ожидания (Ожидается код ошибки 409)
//если при подтверждении данной заявки, лимит заявок для события исчерпан, то все неподтверждённые заявки необходимо отклонить
        return null;
    }
}
