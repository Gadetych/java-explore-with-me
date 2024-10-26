package ru.practicum.ewm.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ViewStatRepositoryTest {
    private final ViewStatRepository repository;
    private final EntityManager em;
    private EndpointHit model1 = new EndpointHit();
    private EndpointHit model2 = new EndpointHit();
    private EndpointHit model3 = new EndpointHit();
    private String app = "ewm-main-service";
    private String uri1 = "/events/1";
    private String uri2 = "/events/2";
    private String ip1 = "192.163.0.1";
    private String ip2 = "192.163.0.2";
    private LocalDateTime now = LocalDateTime.now();
    private LocalDateTime timestamp1 = now.plusMinutes(5);
    private LocalDateTime timestamp2 = now.plusMinutes(10);
    private LocalDateTime timestamp3 = now.plusMinutes(15);

    @BeforeEach
    void setUp() {
        model1.setApp(app);
        model1.setUri(uri1);
        model1.setIp(ip1);
        model1.setTimestamp(timestamp1);

        model2.setApp(app);
        model2.setUri(uri2);
        model2.setIp(ip2);
        model2.setTimestamp(timestamp2);

        model3.setApp(app);
        model3.setUri(uri1);
        model3.setIp(ip1);
        model3.setTimestamp(timestamp3);

        em.persist(model1);
        em.persist(model2);
        em.persist(model3);
    }

    @Test
    void findViewStatsByUri_shouldReturnViewStats() {
        List<String> uris = List.of(uri1, uri2);
        List<ViewStats> result = repository.findViewStatsByUri(now, now.plusDays(1), uris);

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertTrue(result.get(0).getHits() >= result.get(1).getHits());
        assertEquals(2, result.get(0).getHits());
        assertEquals(1, result.get(1).getHits());
        assertEquals(app, result.get(0).getApp());
        assertEquals(uri1, result.get(0).getUri());
    }

    @Test
    void findViewStatsByUriForUniqueIP_shouldReturnViewStatsForUniqueIP() {
        List<String> uris = List.of(uri1, uri2);
        List<ViewStats> result = repository.findViewStatsByUriForUniqueIP(now, now.plusDays(1), uris);

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertTrue(result.get(0).getHits() >= result.get(1).getHits());
        assertEquals(1, result.get(0).getHits());
        assertEquals(1, result.get(1).getHits());
        assertEquals(app, result.get(0).getApp());
        assertEquals(uri1, result.get(0).getUri());
    }

    @Test
    void saveViewStats_shouldTrue() {
        EndpointHit model4 = new EndpointHit();
        model4.setApp(app);
        model4.setUri(uri1);
        model4.setIp(ip1);
        model4.setTimestamp(timestamp3.plusMinutes(5));

        repository.save(model4);

        assertNotNull(model4.getId());
    }
}