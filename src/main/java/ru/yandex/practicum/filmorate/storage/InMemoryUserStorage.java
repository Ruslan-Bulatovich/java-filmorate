package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
    public User updateUser(User user) {
        log.info("Запрос PUT /users " + user);
        if (!(users.containsKey(user.getId()))) {
            return null;
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(Long id) {
        log.info("Запрос GET /users по Id: {}", +id);
        if (!(users.containsKey(id))) {
            return null;
        }
        User user = users.get(id);
        return user;
    }

    @Override
    public User deleteUserById(Long id) {
        log.info("Запрос DELETE /users по Id: {}", +id);
        if (!(users.containsKey(id))) {
            return null;
        }
        User user = users.get(id);
        users.remove(id);
        return user;

    }
}
