package ru.practicum.controller.pub;

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
import ru.practicum.DataTest;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.PublicParamEvent;
import ru.practicum.ewm.stats.client.StatClient;
import ru.practicum.ewm.stats.common.dto.EndpointHitRequestDto;
import ru.practicum.service.EventsService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {PublicEventsController.class})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class PublicEventsControllerTest {
    private final MockMvc mvc;
    private final ObjectMapper mapper;
    @MockBean
    private final EventsService service;
    @MockBean
    private final StatClient client;

    String baseUri = "/events";
    EventFullDto eventFullDtoById1 = DataTest.getEventFullDtoById1();
    EventShortDto eventShortDtoById1 = DataTest.getEventShortDtoById1();

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
    void findAll_whenGivenValidParam_thenReturn200() throws Exception {
        long eventId = eventShortDtoById1.getId();
        when(service.findAll(PublicParamEvent.builder()
                .onlyAvailable(false)
                .from(0)
                .size(10)
                .build())
        ).thenReturn(List.of(eventShortDtoById1));
        mvc.perform(setRequestHeadersWithoutBody(get(String.format("%s", baseUri))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(eventId));
        verify(client, times(1)).save(any(EndpointHitRequestDto.class));
    }

    @Test
    void findById_whenGivenValidParam_thenReturn200() throws Exception {
        long eventId = eventFullDtoById1.getId();
        when(service.findById(eventId)).thenReturn(eventFullDtoById1);
        mvc.perform(setRequestHeadersWithoutBody(get(baseUri + "/" + eventId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventId));
        verify(client, times(1)).save(any(EndpointHitRequestDto.class));
    }
}