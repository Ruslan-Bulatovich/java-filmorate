package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
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
        Optional<User> updatedUser = userStorage.updateUser(user);
        if (!updatedUser.isPresent()) {
            throw new ObjectNotFoundException("Невозможно обновить данные несуществующего пользователя");
        }
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
        User user = getUserById(id);
        User newFriend = getUserById(friendId);
        user.getFriends().add(friendId);
        newFriend.getFriends().add(id);
        return true;
    }

    public Boolean removeFriendship(Long id, Long friendId){
        User user = getUserById(id);
        User removedFriend = getUserById(friendId);
        if (!user.getFriends().contains(friendId) || !removedFriend.getFriends().contains(id)) {
            throw new ObjectNotFoundException("Пользователи не являются друзьями");
        }
        user.getFriends().remove(friendId);
        removedFriend.getFriends().remove(id);
        return true;
    }

    public Collection<User> getFriendsListById(Long id) {
        User user = getUserById(id);
        return user.getFriends().stream().map(this::getUserById).collect(Collectors.toList());
    }

    public Collection<User> getCommonFriendsList(Long userId, Long friendId) {
        User user = getUserById(userId);
        User otherUser = getUserById(friendId);
        return user.getFriends().stream()
                .filter(u -> otherUser.getFriends().contains(u))
                .map(this::getUserById)
                .collect(Collectors.toList());
    }


}
