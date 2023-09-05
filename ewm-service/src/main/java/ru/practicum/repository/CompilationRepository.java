package ru.practicum.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @Query("SELECT c FROM Compilation AS c " +
            "WHERE c.pinned = ?1 OR ?1 IS NULL")
    List<Compilation> findAllByPinned(Boolean pinned, PageRequest pageRequest);
}
