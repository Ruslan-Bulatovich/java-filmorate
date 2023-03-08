package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class FilmService {
    private static final LocalDate DATA_OF_FIRST_FILM = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

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
        Optional<Film> film = filmStorage.getFilmById(id);
        if (!film.isPresent()) {
            throw new ObjectNotFoundException("Фильм с id=" + id + " не найден");
        }
        return film.get();
    }

    public Film deleteFilmById(Long id) {
        Optional<Film> film = filmStorage.deleteFilmById(id);
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
    }

    public void addLike(Long filmId, Long userId) {
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        Optional<User> user = userStorage.getUserById(userId);
        if (!user.isPresent()) {
            throw new ObjectNotFoundException("User с id=" + userId + " не найден");
        }
        filmStorage.removeLike(filmId, userId);
    }

    public Collection<Film> getBestFilms(int count) {
        return filmStorage.getBestFilms(count);
    }
}
