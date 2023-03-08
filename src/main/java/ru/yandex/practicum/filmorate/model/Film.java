package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    @NotNull
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;
    @Size(max = 200, message = "Описание фильма не должно быть больше 200 символов")
    private String description;
    private LocalDate releaseDate;
    @Positive
    private int duration;
    @PositiveOrZero
    private Long id;
    private List<Genre> genres;
    private Set<Long> likes = new HashSet<>();
    private Mpa mpa;


    public Film(Long id, String name, String description, LocalDate releaseDate, int duration, List<Genre> genres, Mpa mpa) {
        this.id = id;
        this.description = description;
        this.releaseDate = releaseDate;
        this.name = name;
        this.duration = duration;
        this.genres = genres;
        this.mpa = mpa;
    }
}
