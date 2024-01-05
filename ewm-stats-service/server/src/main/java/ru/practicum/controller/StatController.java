package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;
import ru.practicum.exception.ValidateDateException;
import ru.practicum.service.StatService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static ru.practicum.Constant.DATE_PATTERN;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatController {

    private final StatService service;

    @PostMapping("/hit")
    @ResponseStatus(value = HttpStatus.CREATED, reason = "Информация сохранена")
    public void saveEndpointHit(@Valid @RequestBody EndpointHit dto) {
        log.info("Сохранение EndpointHit {}", dto);
        service.saveStat(dto);
    }

    @GetMapping("/stats")
    public Collection<ViewStats> getViewStats(
            @RequestParam(value = "start") @DateTimeFormat(pattern = DATE_PATTERN) LocalDateTime start,
            @RequestParam(value = "end") @DateTimeFormat(pattern = DATE_PATTERN) LocalDateTime end,
            @RequestParam(defaultValue = "") List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique
    ) {

        validateDate(start, end);
        log.info("Получение статистики: время начала диапазона {} время конца диапазона {} хосты {} уникальный ip {}",
                start, end, uris, unique);
        return service.getStats(start, end, uris, unique);
    }

    private void validateDate(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new ValidateDateException("Дата окончания не может быть раньше даты начала.");
        }
    }
}
