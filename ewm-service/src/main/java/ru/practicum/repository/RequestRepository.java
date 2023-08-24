package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.Status;

import java.util.List;
import java.util.Set;

@Repository
public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByEventId(long eventId);

    boolean existsByIdAndRequesterId(long requestId, long userId);

    long countAllByEventIdAndStatus(long eventId, Status status);

    List<ParticipationRequest> findAllByEventIdAndStatus(long eventId, Status status);

    List<ParticipationRequest> findAllByRequesterId(long userId);

    List<ParticipationRequest> findAllByIdIn(Set<Long> ids);

}
