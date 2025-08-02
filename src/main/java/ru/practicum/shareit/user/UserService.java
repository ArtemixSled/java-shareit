package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(NewUserRequest request);

    UserDto update(Integer userId, UpdateUserRequest request);

    List<UserDto> findAll();

    UserDto getUser(Integer id);

    UserDto delete(Integer id);

}
