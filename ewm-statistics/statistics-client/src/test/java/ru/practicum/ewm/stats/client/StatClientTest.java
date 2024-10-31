package ru.practicum.ewm.stats.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClientException;
import ru.practicum.ewm.stats.common.dto.EndpointHitRequestDto;
import ru.practicum.ewm.stats.common.dto.ViewStatsResponseDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class StatClientTest {
    public MockWebServer mockBackEnd;
    public StatClient statClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @AfterEach
    void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void initialize() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();

        String baseUrl = String.format("http://localhost:%s",
                mockBackEnd.getPort());
        System.out.println("baseUrl: " + baseUrl);
        statClient = new StatClient(baseUrl, 5000);
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void saveVieStats_whenPostWithBody_thenMockResponseIsCreated() throws InterruptedException, JsonProcessingException {
        String app = "ewm-main-service";
        String uri = "/events/1";
        String ip = "192.163.0.1";
        EndpointHitRequestDto requestDto = EndpointHitRequestDto.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.of(2022, 9, 6, 11, 0, 23))
                .build();
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.CREATED.value())
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        );

        statClient.save(requestDto);
        RecordedRequest recordedRequest = mockBackEnd.takeRequest();

        assertEquals("POST", recordedRequest.getMethod());
        assertEquals("/hit", recordedRequest.getPath());
    }

    @Test
    public void saveVieStats_whenPostWithBody_thenMockResponseIsBadRequest_andSaveThrowsWebClientException() throws JsonProcessingException {
        String app = "ewm-main-service";
        String uri = "/events/1";
        String ip = "192.163.0.1";
        EndpointHitRequestDto requestDto = EndpointHitRequestDto.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.of(2022, 9, 6, 11, 0, 23))
                .build();
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.BAD_REQUEST.value())
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(objectMapper.writeValueAsString(requestDto))
        );

        assertThrows(WebClientException.class, () -> statClient.save(requestDto));
    }

    @Test
    void getViewStats_whenGetWithQueryParams_thenMockResponseIsOkAndBody() throws JsonProcessingException, InterruptedException {
        LocalDateTime start = LocalDateTime.of(2022, 1, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2022, 1, 2, 0, 0, 0);
        List<String> uris = List.of("uri1");

        List<ViewStatsResponseDto> response = List.of(new ViewStatsResponseDto("app", "uris1", 1));

        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(objectMapper.writeValueAsString(response))
        );

        statClient.getViewStats(start, end, uris, true);

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();

        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/stats?start=2022-01-01%2000:00:00&end=2022-01-02%2000:00:00&uris=uri1&unique=true", recordedRequest.getPath());
    }
}