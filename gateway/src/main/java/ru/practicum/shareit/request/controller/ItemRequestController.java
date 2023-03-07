package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.request.client.RequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final RequestClient requestClient;
    private final String sharerUserId = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(sharerUserId) Long userId,
                                                    @Validated(Create.class) @RequestBody ItemRequestDto itemRequestDto) {
        return requestClient.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getForUserRequests(@RequestHeader(sharerUserId) Long userId) {
        return requestClient.getForUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getNotForUserRequests(@RequestHeader(sharerUserId) Long userId,
                                                        @RequestParam(defaultValue = "0") @Min(0) int from,
                                                        @RequestParam(defaultValue = "10") @Min(1) @Max(20) int size) {
        return requestClient.getNotForUserRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader(sharerUserId) Long userId, @PathVariable Long requestId) {
        return requestClient.getItemRequest(userId, requestId);
    }
}
