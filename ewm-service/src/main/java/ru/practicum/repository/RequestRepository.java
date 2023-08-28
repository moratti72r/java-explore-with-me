package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.Status;
import ru.practicum.service.statservice.ConfirmedRequests;

import java.util.List;
import java.util.Set;

@Repository
public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    @Query("SELECT new ru.practicum.service.statservice.ConfirmedRequests(r.event.id , COUNT(r.id)) " +
            "FROM ParticipationRequest AS r " +
            "WHERE r.event.id IN ?1 " +
            "AND r.status = 'CONFIRMED' " +
            "GROUP BY r.event.id ")
    List<ConfirmedRequests> countByEventIdInAndStatusIsConfirmed(List<Long> longs);

    List<ParticipationRequest> findAllByEventId(long eventId);

    boolean existsByIdAndRequesterId(long requestId, long userId);

    long countAllByEventIdAndStatus(long eventId, Status status);

    List<ParticipationRequest> findAllByEventIdAndStatus(long eventId, Status status);

    List<ParticipationRequest> findAllByRequesterId(long userId);

    List<ParticipationRequest> findAllByIdIn(Set<Long> ids);

}
