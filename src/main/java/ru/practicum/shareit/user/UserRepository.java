package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepository implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values();
    }

    public User getUser(Integer id) {
        return users.get(id);
    }

    public User delete(Integer id) {
        users.remove(id);
        return users.get(id);
    }

    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);

        return user;
    }

    public User update(User newUser) {
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    public User findByEmail(String email) {
        return users.values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
