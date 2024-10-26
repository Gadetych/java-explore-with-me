package ru.practicum.ewm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.practicum.ewm.dto.EndpointHitRequestDto;
import ru.practicum.ewm.dto.ViewStatsResponseDto;
import ru.practicum.ewm.service.ViewStatServiceImpl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatServerController.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class StatServerControllerTest {
    private final ObjectMapper objectMapper;
    private final MockMvc mvc;
    @MockBean
    private final ViewStatServiceImpl service;
    String urlHits = "/hit";
    String urlStats = "/stats";
    EndpointHitRequestDto requestDto1 = new EndpointHitRequestDto();
    EndpointHitRequestDto requestDto2 = new EndpointHitRequestDto();
    String app1 = "ewm-main-service";
    String uri1 = "/events/1";
    String ip1 = "192.163.0.1";
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime timestamp1 = now.plusMinutes(5);
    LocalDateTime timestamp2 = now.plusMinutes(10);

    @BeforeEach
    void setUp() {
        requestDto1.setApp(app1);
        requestDto1.setUri(uri1);
        requestDto1.setIp(ip1);
        requestDto1.setTimestamp(timestamp1);

        requestDto2.setApp(app1);
        requestDto2.setUri(uri1);
        requestDto2.setIp(ip1);
        requestDto2.setTimestamp(timestamp2);
    }

    private MockHttpServletRequestBuilder setRequestHeaders(MockHttpServletRequestBuilder builder) throws JsonProcessingException {
        return builder
                .content(objectMapper.writeValueAsString(requestDto1))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON);
    }

    private MockHttpServletRequestBuilder setRequestHeadersWithoutBody(MockHttpServletRequestBuilder builder) throws JsonProcessingException {
        return builder
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON);
    }

    @Test
    void save_shouldStatusCreated() throws Exception {
        mvc.perform(setRequestHeaders(post(urlHits)))
                .andExpect(status().isCreated());
    }

    @Test
    void save_shouldStatusINTERNAL_SERVER_ERROR() throws Exception {
        requestDto1.setApp(null);

        mvc.perform(setRequestHeaders(post(urlHits)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getStats_shouldStatusOKAndNotUniqueIP() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String start = now.format(formatter);
        String end = now.plusDays(1).format(formatter);
        List<String> uris = List.of(uri1);

        when(service.getStats(any(LocalDateTime.class), any(LocalDateTime.class), anyList(), anyBoolean()))
                .thenReturn(List.of(new ViewStatsResponseDto(app1, uri1, 2)));

        mvc.perform(setRequestHeadersWithoutBody(get(urlStats + "?start=" + start + "&end=" + end + "&uris=" + uris)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].app").value(app1))
                .andExpect(jsonPath("$[0].uri").value(uri1))
                .andExpect(jsonPath("$[0].hits").value(2));
    }

    @Test
    void getStats_shouldStatusOKAndUniqueIP() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String start = now.format(formatter);
        String end = now.plusDays(1).format(formatter);
        List<String> uris = List.of(uri1);
        boolean unique = true;

        when(service.getStats(any(LocalDateTime.class), any(LocalDateTime.class), anyList(), anyBoolean()))
                .thenReturn(List.of(new ViewStatsResponseDto(app1, uri1, 1)));

        mvc.perform(setRequestHeadersWithoutBody(get(urlStats + "?start=" + start + "&end=" + end + "&uris=" + uris + "&unique=" + unique)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].app").value(app1))
                .andExpect(jsonPath("$[0].uri").value(uri1))
                .andExpect(jsonPath("$[0].hits").value(1));
    }
}