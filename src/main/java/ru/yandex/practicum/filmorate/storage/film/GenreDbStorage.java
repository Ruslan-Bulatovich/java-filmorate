package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.StorageDb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Component
@Slf4j
@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage, StorageDb {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    @Override
    public Collection<Genre> findAll() {
        String sql = "SELECT * FROM GENRE";
        return jdbcTemplate.query(sql, this::makeGenre);
    }

    @Override
    public Optional<Genre> findById(int id) {
        String sqlQuery = "SELECT * FROM genre WHERE genre_id = ?";
        Genre genre = queryForObjectOrNull(sqlQuery, this::makeGenre, id);
        if (genre == null) {
            return Optional.empty();
        }
        return Optional.of(genre);
    }

    private Genre makeGenre(ResultSet rs, int rn) throws SQLException {
        return new Genre(rs.getInt("genre_id"), rs.getString("genre_name"));
    }
}
