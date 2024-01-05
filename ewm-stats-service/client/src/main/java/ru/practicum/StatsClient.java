package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.dto.EndpointHit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@PropertySource(value = {"classpath:statsClient.properties"})
public class StatsClient {

    @Value("${stats.server.url}")
    private String baseUrl;
    private final WebClient client;

    public StatsClient() {
        this.client = WebClient.create(baseUrl);
    }

    public ResponseEntity<List<ViewStats>> getStats(String start, String end, List<String> urls, Boolean unique) {
        String paramsUri = String.join(",", urls);
        return this.client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParam("uris", paramsUri)
                        .queryParam("unique", unique)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntityList(ViewStats.class)
                .doOnNext(c -> log.info("Получение статистики: время начала диапазона {}, время конца диапазона {}," +
                                "хост {}, уникальный ip {}", start, end, paramsUri, unique))
                .block();
    }

    public void saveStats(String app, String uri, String ip, LocalDateTime timestamp) {
        final EndpointHit endpointHit = new EndpointHit(app, uri, ip, timestamp);

        this.client.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(endpointHit, EndpointHit.class)
                .retrieve()
                .toBodilessEntity()
                .doOnNext(c -> log.info("Сохранение статистики {}", endpointHit))
                .block();
    }
}
