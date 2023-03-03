package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoExt;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class ItemController {
    public static final String sharerUserId = "X-Sharer-User-Id";
    private final ItemService service;

    @PostMapping
    public ItemDto createItem(@RequestHeader(sharerUserId) long userId, @RequestBody ItemDto itemDto) {
        return service.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(sharerUserId) long userId, @PathVariable long itemId, @RequestBody ItemDto itemDto) {
        return service.updateItem(userId, itemId, itemDto);
    }

    @GetMapping
    public List<ItemDtoExt> getAllItemsOfOwner(
            @RequestHeader(sharerUserId) long id,
            @RequestParam(value = "from", defaultValue = "0") int from,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return service.getAllItemsOfOwner(id, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemDtoExt getItemById(@RequestHeader(sharerUserId) long id, @PathVariable long itemId) {
        return service.getItemById(itemId, id);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text,
                                    @RequestParam(value = "from", defaultValue = "0") int from,
                                    @RequestParam(value = "size", defaultValue = "10") int size) {
        return service.searchItem(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(sharerUserId) long userId, @PathVariable long itemId,
                                 @RequestBody CommentDto commentDto) {
        return service.addComment(userId, itemId, commentDto);
    }
}
