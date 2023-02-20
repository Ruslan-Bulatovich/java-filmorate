package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final LocalDate DATA_OF_FIRST_FILM = LocalDate.of(1895, 12, 28);
    private final Map<Long, Film> films = new HashMap<>();
    private Long generatedId = 0L;

    private Long createId() {
        return ++generatedId;
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return films.values();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Запрос POST /films " + film);
        validateDate(film);
        film.setId(createId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Запрос PUT /films " + film);
        if (!(films.containsKey(film.getId()))){
            throw new ValidationException("Фильм не найден");
        }
        validateDate(film);
        films.put(film.getId(), film);
        return film;
    }

    private void validateDate(Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(DATA_OF_FIRST_FILM)) {
            log.debug("Введена неверная дата релиза (раньше 1895-12-28)");
            throw new ValidationException("Введена неверная дата релиза (раньше 1895-12-28)");
        }
        return;
    }
}
