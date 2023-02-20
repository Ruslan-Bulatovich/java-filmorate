package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class Film {
    @NotNull
    @NotBlank(message = "Название фильма не может быть пустым")
    private final String name;
    @Size(max = 200, message = "Описание фильма не должно быть больше 200 символов")
    private final String description;
    private final LocalDate releaseDate;
    @Positive
    private final int duration;
    @PositiveOrZero
    private Long id;
}
