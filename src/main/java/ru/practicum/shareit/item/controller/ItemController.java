package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoExt;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor

public class ItemController {
    public static final String SharerUserId = "X-Sharer-User-Id";
    private final ItemService service;

    @PostMapping
    public ItemDto createItem(@RequestHeader(SharerUserId) long userId, @Valid @RequestBody ItemDto itemDto) {
        return service.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(SharerUserId) long userId, @PathVariable long itemId, @RequestBody ItemDto itemDto) {
        return service.updateItem(userId, itemId, itemDto);
    }

    @GetMapping
    public List<ItemDtoExt> getAllItemsOfOwner(@RequestHeader(SharerUserId) long id) {
        return service.getAllItemsOfOwner(id);
    }

    @GetMapping("/{itemId}")
    public ItemDtoExt getItemById(@RequestHeader(SharerUserId) long id, @PathVariable long itemId) {
        return service.getItemById(itemId, id);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        return service.searchItem(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(SharerUserId) long userId, @PathVariable long itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
        return service.addComment(userId, itemId, commentDto);
    }
}
