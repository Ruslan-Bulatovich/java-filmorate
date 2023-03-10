package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.storage.UtilEmpty.emptyIfNull;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private Long generatedId = 0L;

    private Long createId() {
        return ++generatedId;
    }

    @Override
    public Collection<Film> getAllFilm() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return films.values();
    }

    @Override
    public Collection<Film> getBestFilms(int count) {
        log.info("Запрос GET /films/popular?count={}", count);
        return films.values().stream().sorted((a, b) -> b.getLikes().size() - a.getLikes().size()).
                limit(count).collect(Collectors.toList());
    }

    @Override
    public Film addFilm(Film film) {
        log.info("Запрос POST /films " + film);
        film.setId(createId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        log.info("Запрос PUT /films " + film);
        if (!(films.containsKey(film.getId()))) {
            return Optional.empty();
        }
        films.put(film.getId(), film);
        return Optional.of(film);
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        log.info("Запрос GET /films по Id: {}", +id);
        if (!(films.containsKey(id))) {
            return Optional.empty();
        }
        Film film = films.get(id);
        return Optional.of(film);
    }

    @Override
    public Optional<Film> deleteFilmById(Long id) {
        log.info("Запрос DELETE /films по Id: {}", +id);
        if (!(films.containsKey(id))) {
            return Optional.empty();
        }
        Film film = films.get(id);
        films.remove(id);
        return Optional.of(film);
    }

    @Override
    public void addLike(Long id, Long userId) {
        Film film = getFilmById(id).get();
        Set<Long> likes = emptyIfNull(film.getLikes());
        likes.add(userId);
        film.setLikes(likes);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        emptyIfNull(getFilmById(filmId).get().getLikes()).remove(userId);
    }
}
