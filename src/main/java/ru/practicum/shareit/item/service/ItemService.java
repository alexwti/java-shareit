package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAllItemsOfOwner(long userId);

    ItemDto getItemById(long itemId);

    List<ItemDto> searchItem(String text);

    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}
