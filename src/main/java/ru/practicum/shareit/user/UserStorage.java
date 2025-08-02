package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Collection;

public interface UserStorage {

    User create(User user);
    User update(User user);
    User getUser(Integer id);
    Collection<User> findAll();
    User findByEmail(String email);
    public User delete(Integer id);
}