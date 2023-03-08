package ru.yandex.practicum.filmorate.storage.user;


import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Collection<User> findAll();

    User addUser(User user);

    Optional<User> updateUser(User user);

    Optional<User> getUserById(Long id);

    Optional<User> deleteUserById(Long id);

    boolean addFriend(Long id, Long friendId);

    boolean ackFriend(Long id, Long friendId);

    boolean deleteFriend(Long id, Long friendId);

    List<User> findFriends(Long id);

    List<User> findCommonFriends(Long id1, Long id2);

}
