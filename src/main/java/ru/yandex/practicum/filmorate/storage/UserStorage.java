package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Collection<User> findAll();

    User addUser(User user);

    Optional<User> updateUser(User user);

    Optional<User> getUserById(Long id);

    Optional<User> deleteUserById(Long id);

}
