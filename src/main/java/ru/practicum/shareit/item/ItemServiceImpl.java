package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AuthorizationException;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Override
    public ItemDto create(int ownerId, ItemDto itemDto) {
        User owner = userStorage.getUser(ownerId);
        if (owner == null) {
            throw new ResourceNotFoundException("User id=" + ownerId + " не найден");
        }
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        itemStorage.create(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(int ownerId, int itemId, ItemDto itemDto) {
        Item existing = itemStorage.getById(itemId);
        if (existing == null) {
            throw new ResourceNotFoundException("Item id=" + itemId + " не найден");
        }
        if (existing.getOwner().getId() != ownerId) {
            throw new AuthorizationException("Только владелец может редактировать эту вещь");
        }
        ItemMapper.updateItemFromDto(itemDto, existing);
        itemStorage.update(existing);
        return ItemMapper.toItemDto(existing);
    }

    @Override
    public ItemDto getById(int userId, int itemId) {
        Item item = itemStorage.getById(itemId);
        if (item == null) {
            throw new ResourceNotFoundException("Item id=" + itemId + " не найден");
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllByOwner(int ownerId) {
        if (userStorage.getUser(ownerId) == null) {
            throw new ResourceNotFoundException("User id=" + ownerId + " не найден");
        }
        return itemStorage.findAll().stream()
                .filter(it -> it.getOwner().getId() == ownerId)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        String lower = text.toLowerCase();
        return itemStorage.findAll().stream()
                .filter(Item::isAvailable)
                .filter(it -> it.getName().toLowerCase().contains(lower)
                        || it.getDescription().toLowerCase().contains(lower))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

}
