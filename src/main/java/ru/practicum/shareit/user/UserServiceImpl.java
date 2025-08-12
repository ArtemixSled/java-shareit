package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.mapper.UserMapper.mapToUserDto;
import static ru.practicum.shareit.user.mapper.UserMapper.updateFromRequest;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userRepository;

    @Autowired
    public UserServiceImpl(UserStorage userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto create(NewUserRequest request) {
        User user = UserMapper.mapToUser(request);

        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null) {
            throw new EmailAlreadyExistsException("Email " + request.getEmail() + " уже используется");
        }

        user = userRepository.create(user);
        return mapToUserDto(user);
    }

    public UserDto update(Integer userId, UpdateUserRequest request) {
        User user = userRepository.getUser(userId);

        User existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser != null) {
            throw new EmailAlreadyExistsException("Email " + request.getEmail() + " уже используется");
        }

        updateFromRequest(request, user);
        User updated = userRepository.update(user);
        return UserMapper.mapToUserDto(updated);
    }

    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map((User user) -> mapToUserDto(user))
                .collect(Collectors.toList());
    }

    public UserDto getUser(Integer id) {
        return Optional.ofNullable(userRepository.getUser(id))
                .map(UserMapper::mapToUserDto)
                .orElse(null);
    }

    public UserDto delete(Integer id) {
        return Optional.ofNullable(userRepository.getUser(id))
                .map(UserMapper::mapToUserDto)
                .orElse(null);
    }
}
