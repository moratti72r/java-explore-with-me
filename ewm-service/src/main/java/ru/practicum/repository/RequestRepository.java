package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.Status;
import ru.practicum.service.statservice.ConfirmedRequests;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    @Query("SELECT new ru.practicum.service.statservice.ConfirmedRequests(r.event.id , COUNT(r.id)) " +
            "FROM ParticipationRequest AS r " +
            "WHERE r.event.id IN ?1 " +
            "AND r.status = 'CONFIRMED' " +
            "GROUP BY r.event.id ")
    List<ConfirmedRequests> countByEventIdInAndStatusIsConfirmed(List<Long> longs);

    List<ParticipationRequest> findAllByEventId(long eventId);

    Optional<ParticipationRequest> findByIdAndRequesterId(long requestId, long userId);

    long countAllByEventIdAndStatus(long eventId, Status status);

    List<ParticipationRequest> findAllByEventIdAndStatus(long eventId, Status status);

    List<ParticipationRequest> findAllByRequesterId(long userId);

    List<ParticipationRequest> findAllByIdIn(Set<Long> ids);

}
