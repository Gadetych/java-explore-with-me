package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.enums.StatusParticipationRequest;
import ru.practicum.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long>, QuerydslPredicateExecutor<Request> {

    @Query(value = "SELECT COUNT(r.id) " +
            "FROM Request r " +
            "WHERE r.event.id IN (:eventIds) AND r.status = :status " +
            "GROUP BY r.id, r.event.id " +
            "ORDER BY r.id, r.event.id")
    List<Integer> getIdsRequestsByStatus(@Param("eventIds") List<Long> eventIds, @Param("status") StatusParticipationRequest status);

    List<Request> findAllByEventId(long eventId);

    List<Request> findAllByIdIn(List<Long> requestIds);

    List<Request> findAllByStatusInAndEventIdOrderByStatus(List<StatusParticipationRequest> statusList, Long eventId);

    boolean existsByRequesterIdAndEventId(long requesterId, long eventId);
}
