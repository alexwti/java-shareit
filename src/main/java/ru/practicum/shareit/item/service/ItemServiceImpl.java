package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        userStorage.getUserById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id{} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        });
        log.info("Item created");
        return itemStorage.createItem(userId, itemDto);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, Item item) {
        itemStorage.findItemForUpdate(userId, itemId).orElseThrow(() -> {
            log.warn("Вещь с id{} пользователя с id{} не найдена", item, userId);
            throw new NotFoundException("Вещь не найдена");
        });
        log.info("Вещь обновлена");
        return itemStorage.updateItem(userId, itemId, item);
    }

    @Override
    public ItemDto getItemById(long itemId) {
        log.info("Вещь выгружена");
        return itemStorage.getItemById(itemId).orElseThrow(() -> {
            log.warn("Вещь с id{} не найдена", itemId);
            throw new NotFoundException("Вещь не найдена");
        });
    }

    @Override
    public List<ItemDto> getAllItemsOfOwner(long userId) {
        log.info("Вещь пользователя с id{} выгружены", userId);
        return itemStorage.getAllItemsOfOwner(userId);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        log.info("Выдан результат поиска вещи {}", text);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemStorage.searchItem(text);
    }
}
