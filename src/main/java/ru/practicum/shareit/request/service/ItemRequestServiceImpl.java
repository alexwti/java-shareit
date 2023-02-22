package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper = new ItemRequestMapper();
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper = new ItemMapper();

    @Transactional
    @Override
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        });
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = itemRequestMapper.toModel(itemRequestDto, user);
        ;
        ;
        itemRequest = itemRequestRepository.save(itemRequest);
        return itemRequestMapper.toModelDto(itemRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getForUserRequests(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        });

        List<ItemRequestDto> itemRequestsDto = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId).stream()
                .map(itemRequestMapper::toModelDto)
                .collect(Collectors.toList());

        itemRequestsDto.forEach(itemRequestDto -> itemRequestDto.setItems(itemRepository.findAllByRequest_Id(itemRequestDto.getId()).stream()
                        .map(itemMapper::toModelDto)
                        .collect(Collectors.toList())
                )
        );

        return itemRequestsDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getNotForUserRequests(long userId, int from, int size) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        });

        Pageable pageable = PageRequest.of(from / size, size);

        List<ItemRequestDto> itemRequestsDto = itemRequestRepository.findAllByRequesterIdIsNotOrderByCreatedDesc(userId, pageable).stream()
                .map(itemRequestMapper::toModelDto)
                .collect(Collectors.toList());

        itemRequestsDto.forEach(itemRequestDto -> itemRequestDto.setItems(itemRepository.findAllByRequest_Id(itemRequestDto.getId()).stream()
                        .map(itemMapper::toModelDto)
                        .collect(Collectors.toList())
                )
        );

        return itemRequestsDto;
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDto getItemRequest(Long userId, Long requestId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        });

        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> {
            log.warn("Запрос с id {} не найден", userId);
            throw new NotFoundException("Запрос не найден");
        });
        ItemRequestDto itemRequestDto = itemRequestMapper.toModelDto(itemRequest);

        itemRequestDto.setItems(itemRepository.findAllByRequest_Id(itemRequestDto.getId()).stream()
                .map(itemMapper::toModelDto)
                .collect(Collectors.toList())
        );
        return itemRequestDto;
    }
}
