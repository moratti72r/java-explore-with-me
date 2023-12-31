package ru.practicum.service.compilationservice;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.mapper.CompilationMapper;
import ru.practicum.dto.mapper.EventMapper;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.NotMeetConditionException;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.statservice.StatService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CompilationServiceImpl implements CompilationService {

    CompilationRepository compilationRepository;

    EventRepository eventRepository;

    StatService statService;

    @Override
    public CompilationDto createCompilation(NewCompilationDto compilationDto) {
        Set<Event> eventSet = new HashSet<>();

        if (compilationDto.getEvents() != null) {
            eventSet = getEventsFromIds(compilationDto.getEvents());
        }

        Compilation newCompilation = Compilation.builder()
                .events(eventSet)
                .pinned(compilationDto.isPinned())
                .title(compilationDto.getTitle())
                .build();

        Compilation compilation = compilationRepository.save(newCompilation);

        log.info("Подборка с id={} создана", compilation.getId());

        CompilationDto result = CompilationMapper.toCompilationDto(compilation);
        result.setEvents(getEventShortDtoSet(eventSet));

        return result;
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
    public CompilationDto upDateCompilation(long idCompilation, UpdateCompilationDto compilationDto) {

        Compilation compilation = compilationRepository.findById(idCompilation).orElseThrow(() -> new NotFoundException("Compilation with id=" + idCompilation + " was not found"));
        Set<Event> eventSet;
        if (compilationDto.getEvents() != null) {
            eventSet = getEventsFromIds(compilationDto.getEvents());
            compilation.setEvents(eventSet);
        }
        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }
        if (compilationDto.getTitle() != null && !compilationDto.getTitle().isBlank()) {
            compilation.setTitle(compilationDto.getTitle());
        }

        compilationRepository.save(compilation);

        CompilationDto result = CompilationMapper.toCompilationDto(compilation);
        result.setEvents(getEventShortDtoSet(compilation.getEvents()));

        log.info("Подборка с id={} обновлена", idCompilation);
        return result;
    }

    @Override
    public List<CompilationDto> getComplicationByPinned(Boolean pinned, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by("id").ascending());

        List<Compilation> compilations = compilationRepository.findAllByPinned(pinned, pageRequest);

        Set<Event> events = new HashSet<>();
        for (Compilation comp : compilations) {
            events.addAll(comp.getEvents());
        }
        Map<Long, Long> confirmedReq = statService.getConfirmedRequests(new ArrayList<>(events));
        Map<Long, Long> views = statService.getViews(new ArrayList<>(events));
        Map<Long, CompilationDto> compilationDtoMap = compilations.stream()
                .map(CompilationMapper::toCompilationDto).collect(Collectors.toMap(CompilationDto::getId, compilationDto -> compilationDto));

        for (Compilation compilation : compilations) {
            Set<EventShortDto> eventShortSet = compilation.getEvents().stream().map(EventMapper::toEventShortDto).collect(Collectors.toSet());
            eventShortSet.forEach(eventShortDto -> {
                eventShortDto.setConfirmedRequests(confirmedReq.getOrDefault(eventShortDto.getId(), 0L));
                eventShortDto.setViews(views.getOrDefault(eventShortDto.getId(), 0L));
            });

            compilationDtoMap.get(compilation.getId()).setEvents(eventShortSet);
        }

        log.info("Получен список подборок");
        return new ArrayList<>(compilationDtoMap.values());
    }

    @Override
    public CompilationDto getComplicationById(long idCompilation) {
        Compilation compilation = compilationRepository.findById(idCompilation).orElseThrow(() -> new NotFoundException("Compilation with id=" + idCompilation + " was not found"));

        CompilationDto result = CompilationMapper.toCompilationDto(compilation);
        result.setEvents(getEventShortDtoSet(compilation.getEvents()));

        log.info("Получена подборка с id={}", idCompilation);
        return result;
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

    private Set<EventShortDto> getEventShortDtoSet(Set<Event> eventSet) {
        Set<EventShortDto> result;
        List<Event> events = new ArrayList<>(eventSet);
        if (!events.isEmpty()) {

            Map<Long, Long> confirmedRequest = statService.getConfirmedRequests(events);

            Map<Long, Long> views = statService.getViews(events);

            result = events.stream()
                    .map(EventMapper::toEventShortDto)
                    .collect(Collectors.toSet());
            result.forEach(eventShortDto -> {
                eventShortDto.setConfirmedRequests(confirmedRequest.getOrDefault(eventShortDto.getId(), 0L));
                eventShortDto.setViews(views.getOrDefault(eventShortDto.getId(), 0L));
            });

            return result;
        } else return Set.of();
    }
}
