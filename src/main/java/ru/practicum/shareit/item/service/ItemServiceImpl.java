package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoExt;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
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
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

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
        bookingRepository.findByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now())
                .orElseThrow(() -> new BadRequestException("У вас нет прав комментировать эту вещь"));
        Comment comment = commentMapper.toModel(user, item, commentDto);
        return commentMapper.toModelDto(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDtoExt getItemById(long itemId, long userId) {
        log.info("Вещь выгружена");
        ItemDtoExt itemDtoExt = itemMapper.toModelDtoExt(itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("Вещь с id {} не найдена", itemId);
            throw new NotFoundException("Вещь не найдена");
        }));
        return getBookings(getComments(itemDtoExt, itemId), userId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDtoExt> getAllItemsOfOwner(long userId) {
        log.info("Вещь пользователя с id {} выгружены", userId);
        return itemRepository.findAllByOwnerIdOrderById(userId).stream()
                .map(itemMapper::toModelDtoExt)
                .map(itemDtoExt -> getBookings(itemDtoExt, userId))
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

    private ItemDtoExt getBookings(ItemDtoExt itemDtoExt, long userId) {
        if (itemDtoExt.getOwnerId() == userId) {
            itemDtoExt.setLastBooking(
                    bookingRepository.findLastBooking(
                            itemDtoExt.getId(), LocalDateTime.now()
                    ).map(bookingMapper::toModelDto).orElse(null));
            itemDtoExt.setNextBooking(
                    bookingRepository.findNextBooking(
                            itemDtoExt.getId(), LocalDateTime.now()
                    ).map(bookingMapper::toModelDto).orElse(null));
        } else {
            itemDtoExt.setLastBooking(null);
            itemDtoExt.setNextBooking(null);
        }
        return itemDtoExt;
    }

    private ItemDtoExt getComments(ItemDtoExt itemDtoExt, long itemId) {
        List<CommentDto> commentDto = commentRepository.findAllByItemId(itemId).stream()
                .map(commentMapper::toModelDto)
                .collect(Collectors.toList());
        itemDtoExt.setComments(commentDto);
        return itemDtoExt;
    }
}
