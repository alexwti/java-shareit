package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
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

    private final ItemMapper itemMapper = new ItemMapper();
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper = new CommentMapper();
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper = new BookingMapper();

    @Transactional
    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        userRepository.findById(userId).orElseThrow(() -> {
            log.warn("???????????????????????? ?? id {} ???? ????????????", userId);
            throw new NotFoundException("???????????????????????? ???? ????????????");
        });
        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            itemRequest = itemRequestRepository.findById(itemDto.getRequestId()).orElseThrow(() -> new NotFoundException("???????????? ???? ????????????"));
        }
        Item item = itemMapper.toModel(itemDto);
        item.setRequest(itemRequest);
        item.setOwnerId(userId);
        log.info("Item created");
        return itemMapper.toModelDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Item item = itemMapper.toModel(itemDto);
        Item updItem = itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("???????? {} ?? id {} ???? ??????????????", item, itemId);
            throw new NotFoundException("???????? ???? ??????????????");
        });
        if (userId != updItem.getOwnerId()) {
            log.warn("???????? {} ?? ???????????????????????? ?? id {} ???? ??????????????", item, userId);
            throw new NotFoundException("???????? ?? ???????????????????????? ???? ??????????????");
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

        log.info("???????? {} ??????????????????", item);
        return itemMapper.toModelDto(itemRepository.save(updItem));
    }

    @Transactional
    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("???????????????????????? ?? id {} ???? ????????????", userId);
            throw new NotFoundException("???????????????????????? ???? ????????????");
        });

        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("???????? ?? id {} ???? ??????????????", itemId);
            throw new NotFoundException("???????? ???? ??????????????");
        });
        bookingRepository.findByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now()).orElseThrow(() -> new BadRequestException("?? ?????? ?????? ???????? ???????????????????????????? ?????? ????????"));
        Comment comment = commentMapper.toModel(user, item, commentDto);
        return commentMapper.toModelDto(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDtoExt getItemById(long itemId, long userId) {
        log.info("???????? ??????????????????");
        ItemDtoExt itemDtoExt = itemMapper.toModelDtoExt(itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("???????? ?? id {} ???? ??????????????", itemId);
            throw new NotFoundException("???????? ???? ??????????????");
        }));
        return getBookings(getComments(itemDtoExt, itemId), userId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDtoExt> getAllItemsOfOwner(long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        log.info("???????? ???????????????????????? ?? id {} ??????????????????", userId);
        return itemRepository.findAllByOwnerIdOrderById(userId, pageable).stream().map(itemMapper::toModelDtoExt).map(itemDtoExt -> getBookings(itemDtoExt, userId)).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> searchItem(String text, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        log.info("?????????? ?????????????????? ???????????? ???????? {}", text);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findByNameOrDescriptionLike(text.toLowerCase(), pageable).stream().map(itemMapper::toModelDto).collect(Collectors.toList());
    }

    @Override
    public ItemDtoExt getBookings(ItemDtoExt itemDtoExt, long userId) {
        if (itemDtoExt.getOwnerId() == userId) {

            itemDtoExt.setLastBooking(bookingRepository.findFirstByItemIdAndEndBeforeOrderByStartDesc(itemDtoExt.getId(), LocalDateTime.now()).map(bookingMapper::toModelDto).orElse(null));
            itemDtoExt.setNextBooking(bookingRepository.findFirstByItemIdAndEndIsAfterOrderByStartAsc(itemDtoExt.getId(), LocalDateTime.now()).map(bookingMapper::toModelDto).orElse(null));
        } else {
            itemDtoExt.setLastBooking(null);
            itemDtoExt.setNextBooking(null);
        }
        return itemDtoExt;
    }

    @Override
    public ItemDtoExt getComments(ItemDtoExt itemDtoExt, long itemId) {
        List<CommentDto> commentDto = commentRepository.findAllByItemId(itemId).stream().map(commentMapper::toModelDto).collect(Collectors.toList());
        itemDtoExt.setComments(commentDto);
        return itemDtoExt;
    }
}
