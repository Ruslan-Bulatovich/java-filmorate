package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.StorageDb;

import java.sql.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.storage.UtilEmpty.emptyIfNull;

@Slf4j
@Component
@RequiredArgsConstructor
@Primary
public class FilmDbStorage implements FilmStorage, StorageDb {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        final String sqlQuery = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        final String sqlGenreQuery = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";

        Mpa mpa = film.getMpa();

        KeyHolder generatedId = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            final PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            if (mpa != null) {
                stmt.setInt(5, mpa.getId());
            }
            return stmt;
        }, generatedId);
        film.setId((long) Objects.requireNonNull(generatedId.getKey()).intValue());

        addFilmGenres(film, sqlGenreQuery);

        if (mpa != null) {
            film.setMpa(findMpa(mpa.getId()));
        }

        film.setGenres(findGenre(film.getId()));

        log.info("добавлен фильм {}", film.getName());
        return film;
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        final String sqlQuery =
                "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, MPA_ID = ? " +
                        "WHERE id = ?";
        if (jdbcTemplate.update(sqlQuery,
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(),
                film.getId()) == 0) {
            return Optional.empty(); // not found
        }

        if (film.getGenres() != null) {
            final String sqlDeleteGenreQuery = "DELETE FROM film_genre WHERE film_id = ?";
            final String sqlUpdateGenreQuery = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            jdbcTemplate.update(sqlDeleteGenreQuery, film.getId());
            for (Genre genre : film.getGenres()) {
                updateIgnoreDuplicate(sqlUpdateGenreQuery, film.getId(), genre.getId());
            }
        }

        Mpa mpa = film.getMpa();
        if (mpa != null) {
            film.setMpa(findMpa(mpa.getId()));
        }

        film.setGenres(findGenre(film.getId()));
        log.info("Фильм {} с id = {} обновлен", film.getName(), film.getId());
        return Optional.of(film);
    }

    @Override
    public Optional<Film> deleteFilmById(Long id) {
        Film film = getFilmById(id).get();
        final String sqlDeleteQuery = "DELETE FROM films WHERE id = ?";
        final String sqlDeleteGenreQuery = "DELETE FROM film_genre WHERE film_id = ?";
        final String sqlDeleteLikesQuery = "DELETE FROM films_likes WHERE film_id = ?";
        jdbcTemplate.update(sqlDeleteQuery, id);
        jdbcTemplate.update(sqlDeleteGenreQuery, id);
        jdbcTemplate.update(sqlDeleteLikesQuery, id);
        log.info("Фильм {} с id = {} удален", film.getName(), film.getId());
        return Optional.of(film);
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        final String sqlQuery = "SELECT * FROM films WHERE id = ?";
        log.info("Запрос фильма с id = {} отправлен", id);
        Film film = queryForObjectOrNull(sqlQuery, this::makeFilm, id);
        if (film == null) {
            return Optional.empty();
        }
        return Optional.of(film);
    }

    @Override
    public Collection<Film> getBestFilms(int count) {
        final String sqlQuery =
                "SELECT f.* FROM films f " +
                        "LEFT JOIN films_likes fl ON f.id = fl.film_id " +
                        "GROUP BY f.id " +
                        "ORDER BY COUNT(fl.user_id) DESC " +
                        "LIMIT ?";
        log.info("Запрос {} популярных фильмов выполнен", count);
        return jdbcTemplate.query(sqlQuery, this::makeFilm, count);
    }

    @Override
    public Collection<Film> getAllFilm() {
        final String sqlQuery = "SELECT * FROM films";
        log.info("запрос чтения всех фильмов отправлен");
        return jdbcTemplate.query(sqlQuery, this::makeFilm);
    }


    @Override
    public void removeLike(Long filmId, Long userId) {
        final String sqlQuery = "DELETE FROM films_likes WHERE film_id = ? AND user_id = ?";
        update(sqlQuery, filmId, userId);
        log.info("Удален лайк к фильмы {} от юзера {}", filmId, userId);
    }

    @Override
    public void addLike(Long id, Long userId) {
        final String sqlQuery = "INSERT INTO films_likes (film_id, user_id) VALUES ( ?, ?)";
        update(sqlQuery, id, userId);
        log.info("Добавлен лайк к фильмы {} от юзера {}", id, userId);
    }


    private List<Genre> findGenre(long id) {
        final String sqlQuery =
                "SELECT genre.genre_id, genre_name " +
                        "FROM genre " +
                        "LEFT JOIN film_genre fg ON genre.genre_id = fg.genre_id " +
                        "WHERE film_id = ?";
        return jdbcTemplate.query(sqlQuery, this::makeGenre, id);
    }

    private Mpa findMpa(int id) {
        final String sqlQuery = "SELECT mpa_id, mpa_name FROM mpa WHERE MPA_ID = ?";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        mpaRows.next();
        return new Mpa(mpaRows.getInt("mpa_id"), mpaRows.getString("mpa_name"));
    }


    private Genre makeGenre(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("genre_id");
        String name = resultSet.getString("genre_name");
        return new Genre(id, name);
    }

    private Film makeFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        LocalDate releaseDate = resultSet.getDate("release_date").toLocalDate();
        int duration = resultSet.getInt("duration");

        List<Genre> genres = findGenre(id);

        int mpaId = resultSet.getInt("mpa_id");
        Mpa mpa = resultSet.wasNull() ? null : findMpa(mpaId);

        return new Film(id, name, description, releaseDate, duration, genres, mpa);
    }

    private void addFilmGenres(Film film, String sqlGenreQuery) {
        Connection connection = DataSourceUtils.getConnection(jdbcTemplate.getDataSource());
        try {
            connection.setAutoCommit(false);
            PreparedStatement stmtGenre = connection.prepareStatement(sqlGenreQuery);
            for (Genre genre : emptyIfNull(film.getGenres())) {
                stmtGenre.setLong(1, film.getId());
                stmtGenre.setInt(2, genre.getId());
                stmtGenre.addBatch();
            }
            try {
                stmtGenre.executeBatch();
                connection.commit();
            } catch (BatchUpdateException ex) {
                ex.getMessage();
                connection.rollback();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
