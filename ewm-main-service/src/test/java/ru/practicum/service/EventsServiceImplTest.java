package ru.practicum.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.enums.StateActionUser;
import ru.practicum.enums.StateOfPublication;
import ru.practicum.enums.StatusParticipationRequest;
import ru.practicum.ewm.stats.client.StatClient;
import ru.practicum.ewm.stats.common.dto.ViewStatsResponseDto;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.model.User;
import ru.practicum.repository.CategoriesRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UsersRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventsServiceImplTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private StatClient statClient;
    @Mock
    private CategoriesRepository categoriesRepository;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private RequestRepository requestRepository;
    @InjectMocks
    private EventsServiceImpl service;

    Event event1 = Event.builder()
            .id(1L)
            .annotation("Excepturi quia voluptatem aut veniam atque molestiae ut. Laboriosam id dignissimos nisi nam. Labore voluptas " +
                    "a sint sint nulla sed accusantium nulla. Eum sint et repellat. Maxime et deserunt tempora.")
            .category(Category.builder()
                    .id(1L)
                    .name("invoice1008")
                    .build())
            .initiator(User.builder()
                    .id(1L)
                    .name("Laurence Kuvalis")
                    .email("Terrance24@yahoo.com")
                    .build())
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
            .state(StateOfPublication.PENDING)
            .title("Deserunt corrupti voluptas laboriosam voluptatem delectus excepturi ullam voluptates.")
            .build();

    Event event2 = Event.builder()
            .id(2L)
            .annotation("Vitae omnis delectus. In vitae sunt architecto ut velit. Et voluptatem in facilis quasi dolorem dignissimos. " +
                    "Ullam repudiandae numquam perferendis rem mollitia. Rerum alias eligendi nam id aliquid possimus aspernatur doloremque enim.")
            .category(Category.builder()
                    .id(2L)
                    .name("system-worthy4827")
                    .build())
            .initiator(event1.getInitiator())
            .description("Repellat eligendi eius ad cumque voluptatem illo. Sit assumenda praesentium. Vero beatae doloribus veniam voluptas molestiae consequatur. Enim dolores sapiente vel.\n " +
                    "\rCorrupti qui ullam et quia est totam et. Dolorum et dolores vitae sed accusamus vel ea dolore eaque. Veritatis et perspiciatis consequuntur sit est culpa.\n \rVoluptas veniam natus perspiciatis. Et at qui. " +
                    "Eveniet vel voluptatem vitae enim illo id molestias debitis. Fuga mollitia in. Occaecati nostrum laboriosam culpa dolor labore enim.")
            .eventDate(LocalDateTime.now().plusMonths(2))
            .createdOn(LocalDateTime.now())
            .location(Location.builder()
                    .id(2L)
                    .lat(-67.1219)
                    .lon(31.5753)
                    .build())
            .paid(true)
            .participantLimit(800)
            .requestModeration(true)
            .state(StateOfPublication.PENDING)
            .title("Dolore labore odit cum enim.")
            .build();


    @Test
    void findAll() {
        long userId = 1;
        int from = 0;
        int size = 2;
        when(eventRepository.findAllLimitOrderByCreated(userId, from, size)).thenReturn(List.of(event1, event2));
        int confirmedRequests1 = 1;
        int confirmedRequests2 = 2;
        when(requestRepository.getIdsRequestsByStatus(List.of(event1.getId(), event2.getId()), StatusParticipationRequest.CONFIRMED)).thenReturn(List.of(confirmedRequests1, confirmedRequests2));
        ViewStatsResponseDto viewDto1 = ViewStatsResponseDto.builder()
                .app("event")
                .hits(5)
                .uri("/events/" + event1.getId())
                .build();
        ViewStatsResponseDto viewDto2 = ViewStatsResponseDto.builder()
                .app("event")
                .hits(12)
                .uri("/events/" + event2.getId())
                .build();
        List<ViewStatsResponseDto> viewStats = List.of(viewDto1, viewDto2);
        when(statClient.getViewStats(any(LocalDateTime.class), any(LocalDateTime.class), anyList(), anyBoolean())).thenReturn(viewStats);

        List<EventShortDto> result = service.findAll(event1.getInitiator().getId(), from, size);
        assertNotNull(result);
        assertEquals(2, result.size());
        EventShortDto shortDto1 = result.get(0);
        assertEquals(shortDto1.getId(), event1.getId());
        assertEquals(shortDto1.getAnnotation(), event1.getAnnotation());
        assertEquals(shortDto1.getCategory().getId(), event1.getCategory().getId());
        assertEquals(shortDto1.getCategory().getName(), event1.getCategory().getName());
        assertEquals(shortDto1.getConfirmedRequests(), confirmedRequests1);
        assertEquals(shortDto1.getEventDate(), event1.getEventDate());
        assertEquals(shortDto1.getInitiator().getId(), event1.getInitiator().getId());
        assertEquals(shortDto1.getInitiator().getName(), event1.getInitiator().getName());
        assertEquals(shortDto1.isPaid(), event1.isPaid());
        assertEquals(shortDto1.getTitle(), event1.getTitle());
        assertEquals(shortDto1.getViews(), viewDto1.getHits());

        EventShortDto shortDto2 = result.get(1);
        assertEquals(shortDto2.getId(), event2.getId());
        assertEquals(shortDto2.getAnnotation(), event2.getAnnotation());
        assertEquals(shortDto2.getCategory().getId(), event2.getCategory().getId());
        assertEquals(shortDto2.getCategory().getName(), event2.getCategory().getName());
        assertEquals(shortDto2.getConfirmedRequests(), confirmedRequests2);
        assertEquals(shortDto2.getEventDate(), event2.getEventDate());
        assertEquals(shortDto2.getInitiator().getId(), event2.getInitiator().getId());
        assertEquals(shortDto2.getInitiator().getName(), event2.getInitiator().getName());
        assertEquals(shortDto2.isPaid(), event2.isPaid());
        assertEquals(shortDto2.getTitle(), event2.getTitle());
        assertEquals(shortDto2.getViews(), viewDto2.getHits());
    }

    @Test
    void create() {
        long userId = event1.getInitiator().getId();
        NewEventDto requestBody = NewEventDto.builder()
                .annotation(event1.getAnnotation())
                .category(event1.getCategory().getId())
                .description(event1.getDescription())
                .eventDate(event1.getEventDate())
                .location(LocationDto.builder()
                        .lon(event1.getLocation().getLon())
                        .lat(event1.getLocation().getLat())
                        .build())
                .paid(event1.isPaid())
                .participantLimit(event1.getParticipantLimit())
                .requestModeration(event1.isRequestModeration())
                .title(event1.getTitle())
                .build();
        when(categoriesRepository.findById(requestBody.getCategory())).thenReturn(Optional.ofNullable(event1.getCategory()));
        when(usersRepository.findById(userId)).thenReturn(Optional.ofNullable(event1.getInitiator()));
        when(eventRepository.save(any(Event.class))).thenReturn(event1);
        when(eventRepository.findById(event1.getId())).thenReturn(Optional.ofNullable(event1));
        EventFullDto fullDto = service.create(userId, requestBody);

        assertNotNull(fullDto);
        assertEquals(event1.getId(), fullDto.getId());
        assertEquals(event1.getAnnotation(), fullDto.getAnnotation());
        assertEquals(event1.getCategory().getId(), fullDto.getCategory().getId());
        assertEquals(event1.getCategory().getName(), fullDto.getCategory().getName());
        assertEquals(event1.getDescription(), fullDto.getDescription());
        assertEquals(event1.getEventDate(), fullDto.getEventDate());
        assertEquals(event1.getLocation().getLon(), fullDto.getLocation().getLon());
        assertEquals(event1.getLocation().getLat(), fullDto.getLocation().getLat());
        assertEquals(event1.getParticipantLimit(), fullDto.getParticipantLimit());
        assertEquals(0, fullDto.getConfirmedRequests());
        assertEquals(0, fullDto.getViews());
    }

    @Test
    void findById() {
        long userId = event1.getInitiator().getId();
        long eventId = event1.getId();
        boolean unique = false;
        when(eventRepository.findByInitiatorIdAndId(userId, eventId)).thenReturn(Optional.ofNullable(event1));
        when(requestRepository.getIdsRequestsByStatus(List.of(eventId), StatusParticipationRequest.CONFIRMED)).thenReturn(List.of(1));
        when(statClient.getViewStats(event1.getCreatedOn(), event1.getEventDate(), List.of("/events/" + eventId), unique)).thenReturn(List.of());
        EventFullDto fullDto = service.findById(userId, eventId);

        assertNotNull(fullDto);
        assertEquals(event1.getId(), fullDto.getId());
        assertEquals(event1.getAnnotation(), fullDto.getAnnotation());
        assertEquals(event1.getCategory().getId(), fullDto.getCategory().getId());
        assertEquals(event1.getCategory().getName(), fullDto.getCategory().getName());
        assertEquals(event1.getDescription(), fullDto.getDescription());
        assertEquals(event1.getEventDate(), fullDto.getEventDate());
        assertEquals(event1.getLocation().getLon(), fullDto.getLocation().getLon());
        assertEquals(event1.getLocation().getLat(), fullDto.getLocation().getLat());
        assertEquals(event1.getParticipantLimit(), fullDto.getParticipantLimit());
        assertEquals(1, fullDto.getConfirmedRequests());
        assertEquals(0, fullDto.getViews());
    }

    @Test
    void update() {
        long userId = event1.getInitiator().getId();
        long eventId = event1.getId();
        boolean unique = false;
        UpdateEventUserRequest updateEventUserRequest = UpdateEventUserRequest.builder()
                .stateAction(StateActionUser.CANCEL_REVIEW)
                .build();
        when(eventRepository.findById(eventId)).thenReturn(Optional.ofNullable(event1));
        when(eventRepository.save(any(Event.class))).thenReturn(event1);
        when(requestRepository.getIdsRequestsByStatus(List.of(eventId), StatusParticipationRequest.CONFIRMED)).thenReturn(List.of(1));
        when(statClient.getViewStats(event1.getCreatedOn(), event1.getEventDate(), List.of("/events/" + eventId), unique)).thenReturn(List.of());
        EventFullDto fullDto = service.update(userId, eventId, updateEventUserRequest);

        assertNotNull(fullDto);
        assertEquals(event1.getId(), fullDto.getId());
        assertEquals(event1.getAnnotation(), fullDto.getAnnotation());
        assertEquals(event1.getCategory().getId(), fullDto.getCategory().getId());
        assertEquals(event1.getCategory().getName(), fullDto.getCategory().getName());
        assertEquals(event1.getDescription(), fullDto.getDescription());
        assertEquals(event1.getEventDate(), fullDto.getEventDate());
        assertEquals(event1.getLocation().getLon(), fullDto.getLocation().getLon());
        assertEquals(event1.getLocation().getLat(), fullDto.getLocation().getLat());
        assertEquals(event1.getParticipantLimit(), fullDto.getParticipantLimit());
        assertEquals(1, fullDto.getConfirmedRequests());
        assertEquals(0, fullDto.getViews());
        assertEquals(StateOfPublication.CANCELED, fullDto.getState());
    }
}