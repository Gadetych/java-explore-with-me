package ru.practicum.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.dto.request.ConfirmedRequest;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.enums.StateOfPublication;
import ru.practicum.enums.StatusParticipationRequest;
import ru.practicum.exception.conflict.RequestModificationException;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.model.QRequest;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UsersRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UsersRepository usersRepository;
    @InjectMocks
    private RequestServiceImpl requestService;

    User user1 = User.builder()
            .id(1L)
            .name("Sheila O'Connell")
            .email("Roxane.Upton@yahoo.com")
            .build();

    User user2 = User.builder()
            .id(2L)
            .name("Kuka O'Connell")
            .email("kuka@yahoo.com")
            .build();

    Event event1 = Event.builder()
            .id(1L)
            .annotation("Excepturi quia voluptatem aut veniam atque molestiae ut. Laboriosam id dignissimos nisi nam. Labore voluptas " +
                    "a sint sint nulla sed accusantium nulla. Eum sint et repellat. Maxime et deserunt tempora.")
            .category(Category.builder()
                    .id(1L)
                    .name("invoice1008")
                    .build())
            .initiator(user1)
            .description("Earum voluptas repellendus in necessitatibus necessitatibus et dolores dolor. Assumenda tempore suscipit quibusdam dolor ut consequatur " +
                    "itaque quos veniam. Unde omnis totam inventore a consequatur. Illum ab ut vitae dolorum maiores libero.\n \rEa quas maiores voluptatum qui pariatur quia soluta voluptatum qui. Iusto aperiam consequatur et enim ullam omnis aut. Reprehenderit nobis non. Sapiente officia voluptas omnis.\n \rOdio ea vitae accusamus necessitatibus. Dolor maiores hic mollitia ut ut " +
                    "omnis ea nulla. Qui nesciunt est et sit. Tempore commodi sit qui dolor nihil. Maiores quaerat cupiditate dicta sunt laborum minima quam.")
            .eventDate(LocalDateTime.now().plusMonths(1))
            .createdOn(LocalDateTime.now())
            .location(Location.builder()
                    .id(1L)
                    .lat(37.2264)
                    .lon(18.6974)
                    .build())
            .paid(true)
            .participantLimit(93)
            .requestModeration(true)
            .state(StateOfPublication.PUBLISHED)
            .title("Deserunt corrupti voluptas laboriosam voluptatem delectus excepturi ullam voluptates.")
            .build();


    Request request1 = Request.builder()
            .id(1L)
            .created(LocalDateTime.now())
            .event(event1)
            .requester(user2)
            .status(StatusParticipationRequest.PENDING)
            .build();

    @Test
    void findAll() {
        long userId = user2.getId();
        BooleanExpression predicate = QRequest.request.requester.id.eq(userId);
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        when(requestRepository.findAll(predicate, sort)).thenReturn(List.of(request1));
        List<ParticipationRequestDto> result = requestService.findAll(userId);

        assertEquals(1, result.size());
    }

    @Test
    void shouldCreateRequest() {
        long userId = user2.getId();
        long eventId = event1.getId();
        when(requestRepository.existsByRequesterIdAndEventId(userId, eventId)).thenReturn(false);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event1));
        when(requestRepository.getConfirmedRequestsByStatus(List.of(eventId), StatusParticipationRequest.CONFIRMED)).thenReturn(List.of());
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(requestRepository.save(any(Request.class))).thenReturn(request1);
        ParticipationRequestDto result = requestService.create(userId, eventId);

        assertNotNull(result);
    }

    @Test
    void whenRepeatRequestCreated_thenRequestModificationExceptionThrow() {
        long userId = user2.getId();
        long eventId = event1.getId();
        when(requestRepository.existsByRequesterIdAndEventId(userId, eventId)).thenReturn(true);

        assertThrows(RequestModificationException.class, () -> requestService.create(userId, eventId));
    }

    @Test
    void whenTheUserCreatesRequestForTheirEvent_thenRequestModificationExceptionThrow() {
        long userId = user1.getId();
        long eventId = event1.getId();
        when(requestRepository.existsByRequesterIdAndEventId(userId, eventId)).thenReturn(false);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event1));

        assertThrows(RequestModificationException.class, () -> requestService.create(userId, eventId));
    }

    @Test
    void whenCreatesRequestWithStatePublished_thenRequestModificationExceptionThrow() {
        long userId = user2.getId();
        long eventId = event1.getId();
        event1.setState(StateOfPublication.PENDING);
        when(requestRepository.existsByRequesterIdAndEventId(userId, eventId)).thenReturn(false);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event1));

        assertThrows(RequestModificationException.class, () -> requestService.create(userId, eventId));
    }

    @Test
    void whenConfirmedRequestEqualsLimit_thenRequestModificationExceptionThrow() {
        long userId = user2.getId();
        long eventId = event1.getId();
        event1.setParticipantLimit(1);
        when(requestRepository.existsByRequesterIdAndEventId(userId, eventId)).thenReturn(false);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event1));
        when(requestRepository.getConfirmedRequestsByStatus(List.of(eventId), StatusParticipationRequest.CONFIRMED)).thenReturn(List.of(new ConfirmedRequest(eventId, 1L)));

        assertThrows(RequestModificationException.class, () -> requestService.create(userId, eventId));
    }

    @Test
    void cancelRequest() {
        long userId = user2.getId();
        long requestId = request1.getId();
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request1));
        request1.setStatus(StatusParticipationRequest.CANCELED);
        when(requestRepository.save(request1)).thenReturn(request1);
        ParticipationRequestDto result = requestService.cancelRequest(userId, requestId);

        assertNotNull(result);
    }
}