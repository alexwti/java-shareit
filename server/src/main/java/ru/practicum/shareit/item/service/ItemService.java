package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoExt;

import java.util.List;

public interface ItemService {
    List<ItemDtoExt> getAllItemsOfOwner(long userId, int from, int size);

    ItemDtoExt getItemById(long itemId, long userId);

    List<ItemDto> searchItem(String text, int from, int size);

    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);

    ItemDtoExt getBookings(ItemDtoExt itemDtoExt, long userId);

    ItemDtoExt getComments(ItemDtoExt itemDtoExt, long itemId);
}
