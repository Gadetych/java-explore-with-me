package ru.practicum.ewm.client;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import ru.practicum.ewm.dto.EndpointHitRequestDto;
import ru.practicum.ewm.dto.ViewStatsResponseDto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Component
public class StatClient {
    private final WebClient webClient;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatClient(@Value("${stat.client.base-url}") String baseUrl, @Value("${stat.client.timeout}") int timeout) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
                .responseTimeout(Duration.ofMillis(timeout))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(timeout, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(timeout, TimeUnit.MILLISECONDS)));

        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT_CHARSET, "UTF-8")
                .baseUrl(baseUrl)
                .build();
    }

    public List<ViewStatsResponseDto> getViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.pathSegment("stats")
                        .queryParam("start", start.format(formatter))
                        .queryParam("end", end.format(formatter))
                        .queryParam("uris", String.join(",", uris))
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ViewStatsResponseDto>>() {
                })
                .block();
    }

    public List<ViewStatsResponseDto> getViewStats(LocalDateTime start, LocalDateTime end, boolean unique) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/stats")
                        .queryParam("start", start.format(formatter))
                        .queryParam("end", end.format(formatter))
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ViewStatsResponseDto>>() {
                })
                .block();
    }

    public void save(EndpointHitRequestDto dto) {
        webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/hit").build())
                .body(Mono.just(dto), EndpointHitRequestDto.class)
//                Пока не понимаю как тут отлавливать исключения и ApiError, и надо ли это делать вообще...
//                .exchangeToMono(clientResponse -> {
//                    if (clientResponse.statusCode().equals(HttpStatus.CREATED)) {
//                        return clientResponse.bodyToMono(Void.class);
//                    } else if (clientResponse.statusCode().is4xxClientError()) {
//                        throw new CustomException(clientResponse.bodyToMono(ApiError.class).block());
//                    }else {
//                        return clientResponse.createException().flatMap(Mono::error);
//                    }
//                })
                .retrieve()
//                вызовет WebClientException, если код состояния равен 4xx (ошибка клиента) или 5xx (ошибка сервера)
                .bodyToMono(Void.class)
                .block();
    }
}
