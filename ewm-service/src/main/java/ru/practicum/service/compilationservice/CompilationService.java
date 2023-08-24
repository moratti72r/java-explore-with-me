package ru.practicum.service.compilationservice;


import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto createCompilation(NewCompilationDto compilationDto);

    void deleteCompilation(long idCompilation);

    CompilationDto upDateCompilation(long idCompilation, NewCompilationDto compilationDto);

    List<CompilationDto> getComplicationByPinned(Boolean pinned, int from, int size);

    CompilationDto getComplicationById(long id);
}
