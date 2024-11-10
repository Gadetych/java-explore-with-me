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
import ru.practicum.service.EventsService;

import java.nio.charset.StandardCharsets;

@WebMvcTest(AdminEventsController.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AdminEventsControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper mapper;
    @MockBean
    private final EventsService service;

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
    void findAll() {
    }

    @Test
    void update() {
    }
}