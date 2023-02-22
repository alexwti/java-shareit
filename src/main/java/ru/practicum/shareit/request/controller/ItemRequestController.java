package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final String SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader(SHARER_USER_ID) Long userId, @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getForUserRequests(@RequestHeader(SHARER_USER_ID) Long userId) {
        return itemRequestService.getForUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getNotForUserRequests(@RequestHeader(SHARER_USER_ID) Long userId,
                                                      @RequestParam(defaultValue = "0") @Min(0) int from,
                                                      @RequestParam(defaultValue = "10") @Min(1) @Max(20) int size) {
        return itemRequestService.getNotForUserRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequest(@RequestHeader(SHARER_USER_ID) Long userId, @PathVariable Long requestId) {
        return itemRequestService.getItemRequest(userId, requestId);
    }
}