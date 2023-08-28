package ru.practicum.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Compilation;

import java.util.List;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @Query("SELECT c FROM Compilation AS c " +
            "WHERE c.pinned = ?1 OR (c.pinned = false OR c.pinned = true)")
    List<Compilation> findAllByPinned(Boolean pinned, PageRequest pageRequest);
}
