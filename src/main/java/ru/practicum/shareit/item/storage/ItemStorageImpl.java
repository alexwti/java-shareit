package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ItemStorageImpl implements ItemStorage {
    private final Map<Long, List<Item>> items = new HashMap<>();
    private long id = 1;

    @Override
    public List<Item> getAllItemsOfOwner(long userId) {
        return items.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public Optional<Item> getItemById(long itemId) {
        List<Item> allItems = new ArrayList<>();
        items.forEach((user, itemsOfOwner) -> allItems.addAll(itemsOfOwner));
        return allItems.stream()
                .filter(itemsOfOwner -> itemsOfOwner.getId() == itemId)
                .findFirst();
    }

    @Override
    public Optional<Item> findItemForUpdate(long userId, long itemId) {
        return items.getOrDefault(userId, Collections.emptyList()).stream()
                .filter(itemsOfOwner -> itemsOfOwner.getId() == itemId)
                .findFirst();
    }

    @Override
    public List<Item> searchItem(String text) {
        List<Item> allItems = new ArrayList<>();
        items.forEach((userId, items1) -> allItems.addAll(items.get(userId)));
        return allItems.stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public Item createItem(long userId, Item item) {
        item.setId(id++);
        items.compute(userId, (id, itemsOfOwner) -> {
            if (itemsOfOwner == null) {
                itemsOfOwner = new ArrayList<>();
            }
            itemsOfOwner.add(item);
            return itemsOfOwner;
        });
        return item;
    }

    @Override
    public Item updateItem(long userId, long itemId, Item item) {
        Item updItem = items.get(userId).stream()
                .filter(itemsOfOwner -> itemsOfOwner.getId() == itemId)
                .findFirst().orElseThrow(() -> {
                    log.warn("Вещь с itemId{} не найдена", itemId);
                    throw new NotFoundException("Вещь не найдена");
                });
        if (item.getName() != null) {
            updItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updItem.setAvailable(item.getAvailable());
        }
        items.get(userId).removeIf(item1 -> item1.getId() == itemId);
        items.get(userId).add(updItem);
        return updItem;
    }
}
