package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    @NotBlank(message = "Не заполнена электронная почта")
    @Email(message = "Не правильный формат электронной почты")
    private final String email;
    @NotBlank(message = "Логин пользователя пуст")
    @NotNull(message = "Отсутствует логин пользователя")
    private final String login;
    private final LocalDate birthday;
    @PositiveOrZero
    private Long id;
    private String name;
    private Set<Long> friends = new HashSet<>();
}
