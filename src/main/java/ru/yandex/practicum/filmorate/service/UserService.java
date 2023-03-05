package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;


@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    private void validate(User user) throws ValidationException {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения пользователя не может быть больше текущей даты");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }


    public User createUser(User user) {
        validate(user);
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        validate(user);
        getUserById(user.getId()); // будет ошибка, если не будет найден пользователь
        Optional<User> updatedUser = userStorage.updateUser(user);
        return updatedUser.get();
    }

    public User getUserById(Long id) {
        Optional<User> user = userStorage.getUserById(id);
        if (!user.isPresent()) {
            throw new ObjectNotFoundException("Пользователь с id=" + id + " не найден");
        }
        return user.get();
    }

    public User deleteUserById(Long id) {
        Optional<User> user = userStorage.deleteUserById(id);
        if (!user.isPresent()) {
            throw new ObjectNotFoundException("Пользователь с id=" + id + " не найден");
        }
        return user.get();
    }


    public Boolean addFriendship(Long id, Long friendId) {
        return userStorage.addFriend(id, friendId);
    }

    public Boolean removeFriendship(Long id, Long friendId) {
        return userStorage.deleteFriend(id, friendId);
    }

    public Collection<User> getFriendsListById(Long id) {
        return userStorage.findFriends(id);
    }

    public Collection<User> getCommonFriendsList(Long userId, Long friendId) {
        return userStorage.findCommonFriends(userId, friendId);
    }


}
