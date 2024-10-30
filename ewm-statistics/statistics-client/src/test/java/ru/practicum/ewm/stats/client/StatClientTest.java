package ru.practicum.ewm.stats.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientException;
import ru.practicum.ewm.stats.common.dto.EndpointHitRequestDto;
import ru.practicum.ewm.stats.common.dto.ViewStatsResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class StatClientTest {
    @Value("${wiremock.server.port}")
    private int wireMockPort;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private StatClient statClient;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + wireMockPort;
        System.out.println("WireMock is running on port: " + wireMockPort);
        statClient = new StatClient(baseUrl, 5000);
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void saveVieStats_whenPostWithBody_thenWireMockResponseIsCreated() throws JsonProcessingException {
        stubFor(post(urlEqualTo("/hit"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.CREATED.value())));
        String app = "ewm-main-service";
        String uri = "/events/1";
        String ip = "192.163.0.1";
        EndpointHitRequestDto requestDto = EndpointHitRequestDto.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.of(2022, 9, 6, 11, 0, 23))
                .build();
        statClient.save(requestDto);

        verify(postRequestedFor(urlPathEqualTo("/hit"))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(requestDto))));
    }

    @Test
    public void saveVieStats_whenPostWithBody_thenWireMockResponseIsBadRequest_andSaveThrowsWebClientException() throws JsonProcessingException {
        stubFor(post(urlEqualTo("/hit"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.BAD_REQUEST.value())));
        String app = "ewm-main-service";
        String uri = "/events/1";
        String ip = "192.163.0.1";
        EndpointHitRequestDto requestDto = EndpointHitRequestDto.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.of(2022, 9, 6, 11, 0, 23))
                .build();

        assertThrows(WebClientException.class, () -> statClient.save(requestDto));

        verify(postRequestedFor(urlPathEqualTo("/hit"))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(requestDto))));
    }

    @Test
    void getViewStats_whenGetWithQueryParams_thenWireMockResponseIsOkAndBody() {
        stubFor(get(urlPathEqualTo("/stats"))
                .withQueryParam("start", equalTo("2022-01-01 00:00:00"))
                .withQueryParam("end", equalTo("2022-01-02 00:00:00"))
                .withQueryParam("uris", equalTo("uri1"))
                .withQueryParam("unique", equalTo("true"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"app\": \"app1\", \"uri\": \"uri1\", \"hits\": 2}]")));

        List<ViewStatsResponseDto> response = statClient.getViewStats(
                LocalDateTime.of(2022, 1, 1, 0, 0, 0),
                LocalDateTime.of(2022, 1, 2, 0, 0, 0),
                List.of("uri1"),
                true
        );

        assertNotNull(response);
        assertEquals("app1", response.get(0).getApp());
        assertEquals("uri1", response.get(0).getUri());
        assertEquals(2, response.get(0).getHits());
    }
}