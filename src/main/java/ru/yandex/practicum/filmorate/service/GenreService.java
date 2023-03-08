package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;

import java.util.Collection;
import java.util.Optional;

@Service
public class GenreService {
    private final GenreStorage genreStorage;

    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Genre getGenreById(int id) {
        Optional<Genre> genre = genreStorage.findById(id);
        if (!genre.isPresent()) {
            throw new ObjectNotFoundException("Жанр с идентификатором " + id + " не найден.");
        }
        return genre.get();
    }


    public Collection<Genre> getAll() {
        return genreStorage.findAll();
    }
}