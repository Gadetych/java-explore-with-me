package ru.practicum.controller.closed;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.service.RequestService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PrivateRequestsController.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class PrivateRequestsControllerTest {
    private final MockMvc mvc;
    private final ObjectMapper mapper;
    @MockBean
    private final RequestService service;

    private MockHttpServletRequestBuilder setRequestHeadersWithoutBody(MockHttpServletRequestBuilder builder) {
        return builder
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON);
    }

    @Test
    void findAll_shouldReturnStatusOk() throws Exception {
        long userId = 1L;
        when(service.findAll(userId)).thenReturn(List.of());
        mvc.perform(setRequestHeadersWithoutBody(get(String.format("/users/%d/requests", userId)))).andExpect(status().isOk());
    }

    @Test
    void create_shouldReturnStatusCreated() throws Exception {
        long userId = 1L;
        long eventId = 1L;
        when(service.create(userId, eventId)).thenReturn(new ParticipationRequestDto());
        mvc.perform(setRequestHeadersWithoutBody(post(String.format("/users/%d/requests?eventId=%d", userId, eventId)))).andExpect(status().isCreated());
    }

    @Test
    void update_shouldReturnStatusOk() throws Exception {
        long userId = 1L;
        long requestId = 1L;
        when(service.cancelRequest(userId, requestId)).thenReturn(new ParticipationRequestDto());
        mvc.perform(setRequestHeadersWithoutBody(patch(String.format("/users/%d/requests/%d/cancel", userId, requestId)))).andExpect(status().isOk());
    }
}