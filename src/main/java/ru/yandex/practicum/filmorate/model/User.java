package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @NotBlank(message = "Не заполнена электронная почта")
    @Email(message = "Не правильный формат электронной почты")
    private String email;
    @NotBlank(message = "Логин пользователя пуст")
    @NotNull(message = "Отсутствует логин пользователя")
    private String login;
    private LocalDate birthday;
    @PositiveOrZero
    private Long id;
    private String name;
    private Set<Long> friends = new HashSet<>();

    public User(Long id, @NonNull String email, @NonNull String login, String name, @NonNull LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
