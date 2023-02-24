package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film deleteFilmById(Long id);

    Film getFilmById(Long id);

    Collection<Film> getAllFilm();

    Collection<Film> getBestFilms(int count);
}
