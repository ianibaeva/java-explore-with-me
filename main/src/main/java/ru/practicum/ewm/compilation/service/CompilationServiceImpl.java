package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ObjectNotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.ewm.compilation.mapper.CompilationMapper.toCompilation;
import static ru.practicum.ewm.compilation.mapper.CompilationMapper.toCompilationDto;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);

        List<Compilation> compilations = compilationRepository.findAllByPinned(pinned, pageable);

        return compilations.stream()
                .map(c -> toCompilationDto(c, c.getEvents().stream()
                        .map(EventMapper::toEventShortDto)
                        .collect(Collectors.toList()))).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> {
            throw new ObjectNotFoundException(String.format(
                    "Compilation with ID: %s was not found", compId
            ));
        });

        return toCompilationDto(compilation, compilation.getEvents().stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList()));
    }

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        Set<Event> events = new HashSet<>();

        if (!Objects.isNull(newCompilationDto.getEvents())) {
            events = eventRepository.getByIdIn(newCompilationDto.getEvents());
        }

        Compilation compilation = toCompilation(newCompilationDto, events);

        Compilation savedCompilation = compilationRepository.save(compilation);

        return toCompilationDto(savedCompilation, events.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList()));
    }

    @Override
    public void deleteCompilation(Long compId) {
        compilationRepository.findById(compId).orElseThrow(() -> {
            throw new ObjectNotFoundException(String.format(
                    "Compilation with ID: %s was not found", compId
            ));
        });

        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> {
            throw new ObjectNotFoundException(String.format(
                    "Compilation with ID: %s was not found", compId
            ));
        });

        Set<Event> events = new HashSet<>();

        if (!Objects.isNull(updateCompilationRequest.getEvents())) {
            events = eventRepository.getByIdIn(updateCompilationRequest.getEvents());
        }
        compilation.setEvents(events);

        if (!Objects.isNull(updateCompilationRequest.getTitle())) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }

        if (!Objects.isNull(updateCompilationRequest.getPinned())) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }

        Compilation savedCompilation = compilationRepository.save(compilation);

        return toCompilationDto(savedCompilation, events.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList()));
    }
}
