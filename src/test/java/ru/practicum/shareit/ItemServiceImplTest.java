package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.AuthorizationException;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemStorage itemStorage;
    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private ItemServiceImpl service;

    private User owner;
    private Item storedItem;
    private ItemDto dto;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1);
        owner.setName("Owner");
        owner.setEmail("o@x.com");

        storedItem = Item.builder()
                .id(10)
                .name("Drill")
                .description("Electric drill")
                .available(true)
                .owner(owner)
                .build();

        dto = ItemDto.builder()
                .name("Hammer")
                .description("Heavy hammer")
                .available(true)
                .build();
    }

    @Test
    void create_UserExists_Success() {
        when(userStorage.getUser(1)).thenReturn(owner);
        ItemDto result = service.create(1, dto);

        assertThat(result.getName()).isEqualTo("Hammer");
        assertThat(result.getAvailable()).isTrue();
        verify(itemStorage).create(any(Item.class));
    }

    @Test
    void create_UserNotFound_Throws() {
        when(userStorage.getUser(2)).thenReturn(null);

        assertThatThrownBy(() -> service.create(2, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User id=2");
        verify(itemStorage, never()).create(any());
    }

    @Test
    void update_ItemNotFound_Throws() {
        when(itemStorage.getById(5)).thenReturn(null);
        assertThatThrownBy(() -> service.update(1, 5, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_NotOwner_Throws() {
        // storedItem.owner.id = 1, but call with ownerId=2
        when(itemStorage.getById(10)).thenReturn(storedItem);
        assertThatThrownBy(() -> service.update(2, 10, dto))
                .isInstanceOf(AuthorizationException.class);
    }

    @Test
    void update_Success() {
        when(itemStorage.getById(10)).thenReturn(storedItem);
        dto.setAvailable(false);
        ItemDto updated = service.update(1, 10, dto);

        assertThat(updated.getAvailable()).isFalse();
        assertThat(updated.getName()).isEqualTo("Hammer");
        verify(itemStorage).update(storedItem);
    }

    @Test
    void getById_NotFound_Throws() {
        when(itemStorage.getById(99)).thenReturn(null);
        assertThatThrownBy(() -> service.getById(1, 99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getById_Success() {
        when(itemStorage.getById(10)).thenReturn(storedItem);
        var out = service.getById(1, 10);
        assertThat(out.getId()).isEqualTo(10);
        assertThat(out.getDescription()).isEqualTo("Electric drill");
    }

    @Test
    void getAllByOwner_UserNotFound_Throws() {
        when(userStorage.getUser(3)).thenReturn(null);
        assertThatThrownBy(() -> service.getAllByOwner(3))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getAllByOwner_Success() {
        when(userStorage.getUser(1)).thenReturn(owner);
        when(itemStorage.findAll()).thenReturn(List.of(storedItem));
        var list = service.getAllByOwner(1);
        assertThat(list).hasSize(1)
                .first()
                .extracting(ItemDto::getName)
                .isEqualTo("Drill");
    }

    @Test
    void search_BlankText_ReturnsEmpty() {
        var res = service.search("  ");
        assertThat(res).isEmpty();
    }

    @Test
    void search_FiltersOnlyAvailableAndMatches() {
        Item other = Item.builder()
                .id(11)
                .name("Saw")
                .description("Hand saw")
                .available(false)
                .owner(owner)
                .build();
        when(itemStorage.findAll()).thenReturn(List.of(storedItem, other));

        var res = service.search("drill");
        assertThat(res).hasSize(1)
                .first()
                .extracting(ItemDto::getName)
                .isEqualTo("Drill");
    }
}
