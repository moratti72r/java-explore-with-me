package ru.practicum.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Event;
import ru.practicum.model.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByInitiatorId(long userId, PageRequest pageRequest);

    Optional<Event> findEventByIdAndInitiatorId(long eventId, long userId);

    Set<Event> findAllByIdIn(Set<Long> ids);

    boolean existsByIdAndInitiatorId(long eventId, long userId);

    @Query("SELECT e " +
            "FROM Event AS e " +
            "WHERE (e.initiator.id IN ?1 OR ?1 IS NULL) " +
            "AND (e.state IN ?2 OR ?2 IS NULL) " +
            "AND (e.category.id IN ?3 OR ?3 IS NULL) " +
            "AND (e.eventDate > ?4 OR ?4 IS NULL) " +
            "AND (e.eventDate < ?5 OR ?5 IS NULL) ")
    List<Event> findAllByParameters(List<Long> users, List<State> states, List<Long> categories,
                                    LocalDateTime rangeStart, LocalDateTime rangeEnd, PageRequest pageRequest);


    @Query("SELECT e FROM Event AS e WHERE ((?1 IS NULL) OR ((LOWER(e.annotation) LIKE CONCAT ('%',LOWER(?1),'%')) " +
            "OR (LOWER(e.description) LIKE CONCAT ('%',LOWER(?1),'%')))) " +
            "AND (e.category.id IN (?2) OR ?2 IS NULL) AND (e.paid=?3 OR ?3 IS NULL) AND (e.eventDate >= ?4 OR ?4 IS NULL) " +
            "AND (e.eventDate <= ?5 OR ?5 IS NULL) " +
            "AND (?6 = false OR (?6 = true AND (e.participantLimit > 0))) " +
            "AND (e.state=?7) " +
            "ORDER BY e.id")
    List<Event> findAllByParametersForUser(String text, List<Long> categories, Boolean paid,
                                           LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                           Boolean onlyAvailable, State state,
                                           PageRequest pageRequest);


    Optional<Event> findEventByIdAndState(long id, State state);

}
