package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static ru.yandex.practicum.filmorate.storage.UtilEmpty.emptyIfNull;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private Long generatedId = 0L;

    private Long createId() {
        return ++generatedId;
    }

    @Override
    public Collection<User> findAll() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return users.values();
    }

    @Override
    public User addUser(User user) {
        log.info("Запрос POST /users " + user);
        user.setId(createId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> updateUser(User user) {
        log.info("Запрос PUT /users " + user);
        if (!(users.containsKey(user.getId()))) {
            return Optional.empty();
        }
        users.put(user.getId(), user);
        return Optional.of(user);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        log.info("Запрос GET /users по Id: {}", +id);
        if (!(users.containsKey(id))) {
            return Optional.empty();
        }
        User user = users.get(id);
        return Optional.of(user);
    }

    @Override
    public Optional<User> deleteUserById(Long id) {
        log.info("Запрос DELETE /users по Id: {}", +id);
        if (!(users.containsKey(id))) {
            return Optional.empty();
        }
        User user = users.get(id);
        users.remove(id);
        return Optional.of(user);

    }

    @Override
    public boolean addFriend(Long id, Long friendId) {
        User u = users.get(id);
        if (u == null || users.get(friendId) == null) {
            throw new ObjectNotFoundException("Object not found");
        }
        Set<Long> friends = emptyIfNull(u.getFriends());
        friends.add(friendId);
        u.setFriends(friends);
        return true;
    }

    @Override
    public boolean ackFriend(Long id, Long friendId) {
        return false;
    }

    @Override
    public boolean deleteFriend(Long id, Long friendId) {
        User u = users.get(id);
        if (u == null) {
            return false;
        }
        return emptyIfNull(u.getFriends()).remove(friendId);
    }

    @Override
    public List<User> findFriends(Long id) {
        User u = users.get(id);
        if (u == null) {
            return emptyList();
        }
        return emptyIfNull(u.getFriends()).stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findCommonFriends(Long id1, Long id2) {
        User u1 = users.get(id1);
        User u2 = users.get(id2);
        if (u1 == null || u2 == null) {
            return emptyList();
        }
        Set<Long> friends2 = emptyIfNull(u2.getFriends());
        return emptyIfNull(u1.getFriends()).stream()
                .filter(friends2::contains)
                .map(users::get)
                .collect(Collectors.toList());
    }

}
