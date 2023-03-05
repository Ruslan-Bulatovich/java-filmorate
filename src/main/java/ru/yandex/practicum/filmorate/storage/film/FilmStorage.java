package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Film addFilm(Film film);

    Optional<Film> updateFilm(Film film);

    Optional<Film> deleteFilmById(Long id);

    Optional<Film> getFilmById(Long id);

    Collection<Film> getAllFilm();

    Collection<Film> getBestFilms(int count);

    void addLike(Long id, Long userId);

    void removeLike(Long filmId, Long userId);
}
