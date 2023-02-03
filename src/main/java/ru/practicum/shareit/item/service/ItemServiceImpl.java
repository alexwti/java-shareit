package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Transactional
    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        });
        Item item = itemMapper.toModel(itemDto);
        item.setOwnerId(userId);
        log.info("Item created");
        return itemMapper.toModelDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Item item = itemMapper.toModel(itemDto);
        Item updItem = itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("Вещь {} с id {} не найдена", item, itemId);
            throw new NotFoundException("Вещь не найдена");
        });
        if (userId != updItem.getOwnerId()) {
            log.warn("Вещь {} у пользователя с id {} не найдена", item, userId);
            throw new NotFoundException("Вещь у пользователя не найдена");
        }
        if (item.getName() != null && !item.getName().trim().isEmpty()) {
            updItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().trim().isEmpty()) {
            updItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updItem.setAvailable(item.getAvailable());
        }

        log.info("Вещь {} обновлена", item);
        return itemMapper.toModelDto(itemRepository.save(updItem));
    }

    @Transactional
    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        });

        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("Вещь с id {} не найдена", itemId);
            throw new NotFoundException("Вещь не найдена");
        });
        Comment comment = commentMapper.toModel(user, item, commentDto);
        return commentMapper.toModelDto(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto getItemById(long itemId) {
        log.info("Вещь выгружена");
        return itemMapper.toModelDto(itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("Вещь с id {} не найдена", itemId);
            throw new NotFoundException("Вещь не найдена");
        }));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getAllItemsOfOwner(long userId) {
        log.info("Вещь пользователя с id {} выгружены", userId);
        return itemRepository.findAllByOwnerIdOrderById(userId).stream()
                .map(itemMapper::toModelDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> searchItem(String text) {
        log.info("Выдан результат поиска вещи {}", text);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findByNameOrDescriptionLike(text.toLowerCase()).stream()
                .map(itemMapper::toModelDto)
                .collect(Collectors.toList());
    }
}
