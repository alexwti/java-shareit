package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getForUserRequests(Long userId);

    List<ItemRequestDto> getNotForUserRequests(long userId, int from, int size);

    ItemRequestDto getItemRequest(Long userId, Long requestId);
}
