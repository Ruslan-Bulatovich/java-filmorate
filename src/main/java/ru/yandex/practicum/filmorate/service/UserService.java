package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    private void validate(User user) throws ValidationException {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения пользователя не может быть больше текущей даты");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return;
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
        User updatedUser = userStorage.updateUser(user);
        if (updatedUser == null) {
            throw new ObjectNotFoundException("Невозможно обновить данные несуществующего пользователя");
        }
        return updatedUser;
    }

    public User getUserById(Long id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new ObjectNotFoundException("Пользователь с id=" + id + " не найден");
        }
        return user;
    }

    public User deleteUserById(Long id) {
        return userStorage.deleteUserById(id);
    }


    public Boolean addFriendship(Long id, Long friendId) {
        if (getUserById(id) == null) {
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не существует", id));
        }
        User user = userStorage.getUserById(id);
        if (getUserById(friendId) == null) {
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не существует", friendId));
        }
        User newFriend = userStorage.getUserById(friendId);
        user.getFriends().add(friendId);
        newFriend.getFriends().add(id);
        return true;
    }

    public Boolean removeFriendship(Long id, Long friendId) throws ObjectNotFoundException {
        if (getUserById(id) == null) {
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не существует", id));
        }
        User user = userStorage.getUserById(id);
        if (getUserById(friendId) == null) {
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не существует", friendId));
        }
        User removedFriend = userStorage.getUserById(friendId);
        if (!user.getFriends().contains(friendId) || !removedFriend.getFriends().contains(id)) {
            throw new ObjectNotFoundException("Пользователи не являются друзьями");
        }
        user.getFriends().remove(friendId);
        removedFriend.getFriends().remove(id);
        return true;
    }

    public Collection<User> getFriendsListById(Long id) {
        User user = userStorage.getUserById(id);
        return user.getFriends().stream().map(userStorage::getUserById).collect(Collectors.toList());
    }

    public Collection<User> getCommonFriendsList(Long userId, Long friendId) {
        if (userStorage.getUserById(userId) == null) {
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не существует", userId));
        }
        User user = userStorage.getUserById(userId);
        if (userStorage.getUserById(friendId) == null) {
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не существует", friendId));
        }
        User otherUser = userStorage.getUserById(friendId);
        return user.getFriends().stream()
                .filter(u -> otherUser.getFriends().contains(u))
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }


}
