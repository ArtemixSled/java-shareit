package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(int ownerId, ItemDto itemDto);

    ItemDto update(int ownerId, int itemId, ItemDto itemDto);

    ItemDto getById(int userId, int itemId);

    List<ItemDto> getAllByOwner(int ownerId);

    List<ItemDto> search(String text);

}
