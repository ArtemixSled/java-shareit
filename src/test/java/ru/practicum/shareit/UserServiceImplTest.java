package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private UserServiceImpl service;

    private User existing;
    private NewUserRequest newReq;
    private UpdateUserRequest updReq;

    @BeforeEach
    void init() {
        existing = new User();
        existing.setId(1);
        existing.setName("Alice");
        existing.setEmail("alice@example.com");

        newReq = new NewUserRequest();
        newReq.setName("Bob");
        newReq.setEmail("bob@example.com");

        updReq = new UpdateUserRequest();
        updReq.setId(1);
        updReq.setName("Alice2");
        updReq.setEmail("alice2@example.com");
    }

    @Test
    void create_NewEmail_Success() {
        when(userStorage.findByEmail(newReq.getEmail())).thenReturn(null);
        User toSave = new User();
        toSave.setName("Bob");
        toSave.setEmail("bob@example.com");
        User saved = new User();
        saved.setId(42);
        saved.setName("Bob");
        saved.setEmail("bob@example.com");
        when(userStorage.create(any(User.class))).thenReturn(saved);

        UserDto dto = service.create(newReq);

        assertThat(dto.getId()).isEqualTo(42);
        assertThat(dto.getName()).isEqualTo("Bob");
        assertThat(dto.getEmail()).isEqualTo("bob@example.com");
        verify(userStorage).findByEmail("bob@example.com");
        verify(userStorage).create(any(User.class));
    }

    @Test
    void create_DuplicateEmail_Throws() {
        when(userStorage.findByEmail(existing.getEmail())).thenReturn(existing);
        NewUserRequest dup = new NewUserRequest();
        dup.setName("X");
        dup.setEmail(existing.getEmail());

        assertThatThrownBy(() -> service.create(dup))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("уже используется");

        verify(userStorage).findByEmail(existing.getEmail());
        verify(userStorage, never()).create(any());
    }

    @Test
    void update_NewEmail_Success() {
        when(userStorage.getUser(1)).thenReturn(existing);
        when(userStorage.findByEmail(updReq.getEmail())).thenReturn(null);
        User updated = new User();
        updated.setId(1);
        updated.setName("Alice2");
        updated.setEmail("alice2@example.com");
        when(userStorage.update(existing)).thenReturn(updated);

        UserDto dto = service.update(1, updReq);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("Alice2");
        assertThat(dto.getEmail()).isEqualTo("alice2@example.com");
        verify(userStorage).getUser(1);
        verify(userStorage).findByEmail("alice2@example.com");
        verify(userStorage).update(existing);
    }

    @Test
    void update_DuplicateEmail_Throws() {
        when(userStorage.getUser(1)).thenReturn(existing);
        when(userStorage.findByEmail(existing.getEmail())).thenReturn(new User());

        updReq.setEmail(existing.getEmail());
        assertThatThrownBy(() -> service.update(1, updReq))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userStorage).getUser(1);
        verify(userStorage).findByEmail(existing.getEmail());
        verify(userStorage, never()).update(any());
    }

    @Test
    void findAll_AndGetUser() {
        when(userStorage.findAll()).thenReturn(List.of(existing));
        when(userStorage.getUser(1)).thenReturn(existing);

        List<UserDto> all = service.findAll();
        assertThat(all).hasSize(1)
                .first()
                .extracting(UserDto::getEmail)
                .isEqualTo("alice@example.com");

        UserDto one = service.getUser(1);
        assertThat(one.getName()).isEqualTo("Alice");
    }
}
