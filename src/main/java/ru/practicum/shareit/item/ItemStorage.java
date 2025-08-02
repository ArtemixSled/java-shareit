package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {

    Item create(Item item);
    Item update(Item item);
    Item getById(int id);
    Collection<Item> findAll();
    Item delete(Integer id);
}
