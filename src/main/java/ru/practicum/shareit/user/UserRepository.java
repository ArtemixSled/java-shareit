package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Repository
public class UserRepository implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private final Map<String, Integer> emailIndex = new HashMap<>();

    private static String keyOf(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    public Collection<User> findAll() {
        return users.values();
    }

    public User getUser(Integer id) {
        return users.get(id);
    }

    public User delete(Integer id) {
        User removed = users.remove(id);
        if (removed != null) {
            emailIndex.remove(keyOf(removed.getEmail()));
        }
        return removed;
    }

    public User create(User user) {
        String k = keyOf(user.getEmail());

        user.setId(getNextId());
        users.put(user.getId(), user);
        emailIndex.put(k, user.getId());
        return user;
    }

    public User update(User newUser) {
        User prev = users.put(newUser.getId(), newUser);

        String prevKey = (prev != null) ? keyOf(prev.getEmail()) : null;
        String newKey  = keyOf(newUser.getEmail());

        if (prevKey != null && !prevKey.equals(newKey)) {
            emailIndex.remove(prevKey);
        }

        if (newKey != null) {
            emailIndex.put(newKey, newUser.getId());
        }

        return newUser;
    }

    public User findByEmail(String email) {
        Integer id = emailIndex.get(keyOf(email));
        return id == null ? null : users.get(id);
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
