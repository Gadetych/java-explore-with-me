package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface ViewStatRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select new ru.practicum.ewm.model.ViewStats(e.app, e.uri, count(e.ip)) " +
            "from EndpointHit e " +
            "where e.uri in (:uris) and e.timestamp between :start and :end " +
            "group by e.uri " +
            "order by count(e.ip) desc")
    List<ViewStats> findViewStatsByUri(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("uris") List<String> uris);

    @Query("select new ru.practicum.ewm.model.ViewStats(e.app, e.uri, count(distinct e.ip)) " +
            "from EndpointHit e " +
            "where e.uri in (:uris) and e.timestamp between :start and :end " +
            "group by e.uri " +
            "order by count(e.ip) desc ")
    List<ViewStats> findViewStatsByUriForUniqueIP(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("uris") List<String> uris);
}
