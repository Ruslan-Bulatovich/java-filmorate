package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private static final LocalDate DATA_OF_FIRST_FILM = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Collection<Film> findAll() {
        return filmStorage.getAllFilm();
    }

    public Film addFilm(Film film) {
        validateDate(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        validateDate(film);
        Optional<Film> updatedFilm = filmStorage.updateFilm(film);
        if (!updatedFilm.isPresent()) {
            throw new ObjectNotFoundException("Невозможно обновить данные несуществующего фильма");
        }
        return updatedFilm.get();
    }

    public Film getFilmById(Long id) {
        Optional <Film> film = filmStorage.getFilmById(id);
        if (!film.isPresent()) {
            throw new ObjectNotFoundException("Фильм с id=" + id + " не найден");
        }
        return film.get();
    }

    public Film deleteFilmById(Long id) {
        Optional <Film> film = filmStorage.deleteFilmById(id);
        if (!film.isPresent()) {
            throw new ObjectNotFoundException("Фильм с id=" + id + " не найден");
        }
        return film.get();
    }


    private void validateDate(Film film) {
        if (film.getReleaseDate().isBefore(DATA_OF_FIRST_FILM)) {
            log.debug("Введена неверная дата релиза (раньше 1895-12-28)");
            throw new ValidationException("Введена неверная дата релиза (раньше 1895-12-28)");
        }
        return;
    }

    public Film addLike(Long filmId, Long userId) {
        if (userStorage.getUserById(userId) == null) {
            throw new ObjectNotFoundException("Пользователь с ID: " + userId + " не найден");
        }
        User user = userStorage.getUserById(userId).get();
        Film film = getFilmById(filmId);

        film.getLikes().add(user.getId());
        return film;
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        if (film != null) {
            if (film.getLikes().contains(userId)) {
                film.getLikes().remove(userId);
                filmStorage.updateFilm(film);
            } else {
                throw new ObjectNotFoundException("Like на фильм " + filmId + " от пользователя "
                        + userId + " не найден");
            }
        }
    }

    public Collection<Film> getBestFilms(int count) {
        return filmStorage.getBestFilms(count);
    }
}
