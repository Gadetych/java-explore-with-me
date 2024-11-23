package ru.practicum.controller.privy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.enums.StateActionUser;
import ru.practicum.service.EventsService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PrivateEventsController.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class PrivateEventsControllerTest {
    private final MockMvc mvc;
    private final ObjectMapper objectMapper;
    @MockBean
    private EventsService service;

    String baseUri = "/users/%d/events";

    private MockHttpServletRequestBuilder setRequestHeadersWithoutBody(MockHttpServletRequestBuilder builder) {
        return builder
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON);
    }

    private MockHttpServletRequestBuilder setRequestHeadersWithBody(MockHttpServletRequestBuilder builder, Object requestBody) throws JsonProcessingException {
        return builder
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON);
    }

    @Test
    void findAll_shouldReturnListOfEvents() throws Exception {
        long userId = 1L;
        List<EventShortDto> events = List.of(EventShortDto.builder()
                .id(1L)
                .annotation("This is a short description of the event.")
                .title("Short Event Title")
                .eventDate(LocalDateTime.of(2023, 10, 30, 15, 0))
                .views(100)
                .build());

        when(service.findAll(userId, 0, 10)).thenReturn(events);

        mvc.perform(get(String.format(baseUri, userId))
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));

        verify(service, times(1)).findAll(userId, 0, 10);
    }

    @Test
    void create_shouldReturnCreatedEvent() throws Exception {
        long userId = 1L;
        NewEventDto newEventDto = NewEventDto.builder()
                .annotation("Event annotation. NjJqfjqfgnllljenfjlwbg'ojbwg'oaebg'oaegljnbas'jlgnl'jgn'oajnboaj'ebjanejnbaejgnojegneganegjaegjejgnejg")
                .category(1L)
                .description("Event description.  NjJqfjqfgnllljenfjlwbg'ojbwg'oaebg'oaegljnbas'jlgnl'jgn'oajnboaj'ebjanejnbaejgnojegneganegjaegjejgnejg")
                .eventDate(LocalDateTime.of(2030, 11, 5, 2, 4, 30))
                .location(LocationDto.builder()
                        .lat(14.1352)
                        .lon(3.134)
                        .build())
                .title("Event title")
                .build();

        EventFullDto createdEvent = EventFullDto.builder()
                .id(1L)
                .annotation(newEventDto.getAnnotation())
                .category(CategoryDto.builder()
                        .id(1L)
                        .name("Music")
                        .build())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .location(newEventDto.getLocation())
                .paid(newEventDto.isPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.isRequestModeration())
                .title(newEventDto.getTitle())
                .build();

        when(service.create(anyLong(), any(NewEventDto.class))).thenReturn(createdEvent);

        mvc.perform(setRequestHeadersWithBody(post(String.format(baseUri, userId)), newEventDto))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.annotation").value(newEventDto.getAnnotation()));

        verify(service, times(1)).create(userId, newEventDto);
    }

    @Test
    void findById_shouldReturnEvent() throws Exception {
        long userId = 1L;
        long eventId = 2L;
        EventFullDto event = EventFullDto.builder()
                .id(eventId)
                .annotation("Event annotation")
                .build();

        when(service.findById(userId, eventId)).thenReturn(event);

        mvc.perform(get(String.format(baseUri + "/%d", userId, eventId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventId))
                .andExpect(jsonPath("$.annotation").value("Event annotation"));

        verify(service, times(1)).findById(userId, eventId);
    }

    @Test
    void update_shouldReturnUpdatedEvent() throws Exception {
        long userId = 1L;
        long eventId = 2L;
        UpdateEventUserRequest updateRequest = UpdateEventUserRequest.builder()
                .annotation("This is an updated event annotation that meets the length requirement.")
                .category(1L)
                .description("This is an updated description for the event.")
                .eventDate(LocalDateTime.of(2030, 11, 5, 15, 0))
                .location(new LocationDto(null, 55.331, 5.112))
                .paid(true)
                .participantLimit(100)
                .requestModeration(false)
                .stateAction(StateActionUser.SEND_TO_REVIEW)
                .title("Updated Event Title")
                .build();

        EventFullDto updatedEvent = EventFullDto.builder()
                .id(eventId)
                .annotation("Updated annotation")
                .build();

        when(service.update(userId, eventId, updateRequest)).thenReturn(updatedEvent);

        mvc.perform(setRequestHeadersWithBody(patch(String.format(baseUri + "/%d", userId, eventId)), updateRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventId))
                .andExpect(jsonPath("$.annotation").value("Updated annotation"));

        verify(service, times(1)).update(userId, eventId, updateRequest);
    }

    @Test
    void findRequests() {
    }

    @Test
    void updateResult() {
    }
}