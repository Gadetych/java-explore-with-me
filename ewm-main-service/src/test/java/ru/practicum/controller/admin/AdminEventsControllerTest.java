package ru.practicum.controller.admin;

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
import ru.practicum.dto.event.AdminParamEvent;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.enums.StateActionAdmin;
import ru.practicum.enums.StateOfPublication;
import ru.practicum.service.EventsService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminEventsController.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AdminEventsControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper mapper;
    @MockBean
    private final EventsService service;
    String baseUri = "/admin/events";

    private MockHttpServletRequestBuilder setRequestHeadersWithoutBody(MockHttpServletRequestBuilder builder) {
        return builder
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON);
    }

    private MockHttpServletRequestBuilder setRequestHeadersWithBody(MockHttpServletRequestBuilder builder, Object body) throws JsonProcessingException {
        return builder
                .content(mapper.writeValueAsString(body))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON);
    }

    @Test
    void findALl_whenGivenValidParam_thenReturn200() throws Exception {
        List<Long> users = List.of(1L);
        List<StateOfPublication> states = List.of(StateOfPublication.PENDING);
        List<Long> categories = List.of(1L);
        LocalDateTime rangeStart = LocalDateTime.of(2020, 1, 1, 1, 1);
        LocalDateTime rangeEnd = LocalDateTime.of(2030, 1, 1, 1, 2);
        int from = 0;
        int size = 10;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        when(service.findAll(any(AdminParamEvent.class))).thenReturn(List.of());
        mockMvc.perform(setRequestHeadersWithoutBody(get(String.format("%s?users=%d&states=%s&categories=%d&rangeStart=%s&rangeEnd=%s&from=%d&size=%d", baseUri,
                users.get(0), states.get(0), categories.get(0), rangeStart.format(formatter), rangeEnd.format(formatter), from, size)))).andExpect(status().isOk());
    }

    @Test
    void update_whenGivenValidParam_thenReturn200() throws Exception {
        long eventId = 1L;
        UpdateEventAdminRequest requestDto = UpdateEventAdminRequest.builder()
                .annotation("This is an updated event annotation that meets the length requirement.")
                .category(1L)
                .description("This is an updated description for the event.")
                .eventDate(LocalDateTime.of(2030, 11, 5, 15, 0))
                .location(new LocationDto(null, 55.331, 5.112))
                .paid(true)
                .participantLimit(100)
                .requestModeration(false)
                .stateAction(StateActionAdmin.PUBLISH_EVENT)
                .title("Updated Event Title")
                .build();
        mockMvc.perform(setRequestHeadersWithBody(patch(String.format("%s/%d", baseUri, eventId)), requestDto)).andExpect(status().isOk());
    }
}