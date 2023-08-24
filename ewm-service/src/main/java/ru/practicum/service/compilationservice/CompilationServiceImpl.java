package ru.practicum.service.compilationservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.mapper.CompilationMapper;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.NotMeetConditionException;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;

    private final EventRepository eventRepository;

    @Override
    public CompilationDto createCompilation(NewCompilationDto compilationDto) {
        Set<Event> eventSet;
        if (compilationDto.getEvents() != null) {
            eventSet = getEventsFromIds(compilationDto.getEvents());
        } else throw new NotMeetConditionException("Error: Список идентификаторов событий не должен быть null");

        Compilation compilation = compilationRepository.save(Compilation.builder()
                .events(eventSet)
                .pinned(compilationDto.getPinned())
                .title(compilationDto.getTitle())
                .build());

        log.info("Подборка с id={} создана", compilation.getId());

        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    public void deleteCompilation(long idCompilation) {
        if (!compilationRepository.existsById(idCompilation)) {
            throw new NotFoundException("Compilation with id=" + idCompilation + " was not found");
        }
        compilationRepository.deleteById(idCompilation);
        log.info("Подборка с id={} удалена", idCompilation);
    }

    @Override
    public CompilationDto upDateCompilation(long idCompilation, NewCompilationDto compilationDto) {

        Compilation compilation = compilationRepository.findById(idCompilation).orElseThrow(() -> new NotFoundException("Compilation with id=" + idCompilation + " was not found"));
        Set<Event> eventSet;
        if (compilationDto.getEvents() != null) {
            eventSet = getEventsFromIds(compilationDto.getEvents());
            compilation.setEvents(eventSet);
        }

        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }
        if (compilationDto.getTitle() != null) {
            compilation.setTitle(compilationDto.getTitle());
        }

        compilationRepository.save(compilation);

        log.info("Плдборка с id={} обновлена", idCompilation);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    public List<CompilationDto> getComplicationByPinned(Boolean pinned, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by("id").ascending());
        List<CompilationDto> compilationDtoList;
        if (pinned != null) {
            List<Compilation> compilations = compilationRepository.findAllByPinned(pinned, pageRequest);
            compilationDtoList = compilations.stream().map(CompilationMapper::toCompilationDto).collect(Collectors.toList());
        } else {
            List<Compilation> compilations = (List<Compilation>) compilationRepository.findAll(pageRequest);
            compilationDtoList = compilations.stream().map(CompilationMapper::toCompilationDto).collect(Collectors.toList());
        }

        log.info("Получен список подборок");
        return compilationDtoList;
    }

    @Override
    public CompilationDto getComplicationById(long idCompilation) {
        if (!compilationRepository.existsById(idCompilation)) {
            throw new NotFoundException("Compilation with id=" + idCompilation + " was not found");
        }
        Compilation compilation = compilationRepository.findById(idCompilation).get();

        log.info("Получена подборка с id={}", idCompilation);
        return CompilationMapper.toCompilationDto(compilation);
    }

    private Set<Event> getEventsFromIds(Set<Long> ids) {
        Set<Event> result;
        if (ids.size() == 1 && ids.contains(0L) || ids.isEmpty()) {
            result = Collections.emptySet();
        } else {
            result = eventRepository.findAllByIdIn(ids);
            if (result.size() != ids.size()) {
                throw new NotMeetConditionException("Field: eventDate. Error: Все события должны существовать" +
                        " Value: " + ids);
            }
        }
        return result;
    }
}
