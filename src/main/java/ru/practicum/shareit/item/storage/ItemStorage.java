package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    List<Item> getAllItemsOfOwner(long userId);

    Optional<Item> getItemById(long itemId);

    Optional<Item> findItemForUpdate(long userId, long itemId);

    List<Item> searchItem(String text);

    Item createItem(long userId, Item item);

    Item updateItem(long userId, long itemId, Item item);
}
