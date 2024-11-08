package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE e.initiator.id = :userId " +
            "ORDER BY e.createdOn " +
            "LIMIT :size " +
            "OFFSET :from")
    List<Event> findAllLimitOrderByCreated(@Param("userId") long userId, @Param("from") int from, @Param("size") int size);

    Optional<Event> findByInitiatorIdAndId(long initiatorId, long id);
}
