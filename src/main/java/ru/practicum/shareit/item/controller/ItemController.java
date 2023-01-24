package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor

public class ItemController {
    public static final String SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemService service;

    @PostMapping
    public ItemDto createItem(@RequestHeader(SHARER_USER_ID) long userId, @Valid @RequestBody ItemDto itemDto) {
        return service.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(SHARER_USER_ID) long userId, @PathVariable long itemId, @RequestBody Item item) {
        return service.updateItem(userId, itemId, item);
    }

    @GetMapping
    public List<ItemDto> getAllItemsOfOwner(@RequestHeader(SHARER_USER_ID) long id) {
        return service.getAllItemsOfOwner(id);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId) {
        return service.getItemById(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        return service.searchItem(text);
    }
}
