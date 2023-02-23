package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
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
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserServiceImpl userServiceImpl;

    private User user1;

    private User user2;

    private Item item;

    private Booking booking;

    private Comment comment;

    private LocalDateTime now;
    private ItemMapper itemMapper = new ItemMapper();
    private CommentMapper commentMapper = new CommentMapper();

    @BeforeEach
    void beforeEach() {
        now = LocalDateTime.now();
        LocalDateTime start = now.plusDays(1);
        LocalDateTime end = now.plusDays(2);

        user1 = new User(1L, "User1 name", "user1@yandex.ru");
        user2 = new User(2L, "User2 name", "user2@yandex.ru");

        item = Item.builder()
                .id(1L)
                .name("Item name")
                .description("Item description")
                .available(true)
                .ownerId(user1.getId())
                .request(null)
                .build();


        booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .build();

        comment = Comment.builder()
                .id(1L)
                .text("Comment text")
                .item(item)
                .author(user2)
                .created(now)
                .build();
    }

    @Test
    void getAllItemsOfOwnerTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        when(repository.findAllByOwnerIdOrderById(anyLong()))
                .thenReturn(List.of(item));


        List<ItemDtoExt> itemDtoBooking = itemService.getAllItemsOfOwner(user1.getId());

        assertEquals(1, itemDtoBooking.size());
        assertEquals(1, itemDtoBooking.get(0).getId());
        assertEquals("Item name", itemDtoBooking.get(0).getName());
        assertEquals("Item description", itemDtoBooking.get(0).getDescription());
    }

    @Test
    void getItemByIdTest() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        ItemDtoExt itemDtoExt = itemService.getItemById(
                item.getId(),
                user1.getId());

        assertEquals(1, itemDtoExt.getId());
        assertEquals("Item name", itemDtoExt.getName());
        assertEquals("Item description", itemDtoExt.getDescription());
        assertEquals(true, itemDtoExt.getAvailable());
    }

    @Test
    void searchItemWithNameFirstLetterTest() {
        when(repository.findByNameOrDescriptionLike(anyString()))
                .thenReturn(List.of(item));

        List<ItemDto> itemDtos = itemService.searchItem("Item");

        assertEquals(1, itemDtos.size());
        assertEquals(1, itemDtos.get(0).getId());
        assertEquals("Item name", itemDtos.get(0).getName());
        assertEquals("Item description", itemDtos.get(0).getDescription());
        assertEquals(true, itemDtos.get(0).getAvailable());
        assertNull(itemDtos.get(0).getRequestId());
    }

    @Test
    void searchItemWithBlancTextTest() {

        when(repository.findByNameOrDescriptionLike(anyString()))
                .thenReturn(List.of(item));
        List<ItemDto> itemDtos = itemService.searchItem("");

        assertEquals(Collections.emptyList(), itemDtos);
    }

    @Test
    void searchItemWithNameInRandomUpperCaseTest() {
        when(repository.findByNameOrDescriptionLike(anyString()))
                .thenReturn(List.of(item));

        List<ItemDto> itemDtos = itemService.searchItem("iTem");

        assertEquals(1, itemDtos.size());
        assertEquals(1, itemDtos.get(0).getId());
        assertEquals("Item name", itemDtos.get(0).getName());
        assertEquals("Item description", itemDtos.get(0).getDescription());
        assertEquals(true, itemDtos.get(0).getAvailable());
        assertNull(itemDtos.get(0).getRequestId());
    }

    @Test
    void searchItemWithDescriptionInRandomUpperCaseTest() {
        when(repository.findByNameOrDescriptionLike(anyString()))
                .thenReturn(List.of(item));

        List<ItemDto> itemDtos = itemService.searchItem("desCription");

        assertEquals(1, itemDtos.size());
        assertEquals(1, itemDtos.get(0).getId());
        assertEquals("Item name", itemDtos.get(0).getName());
        assertEquals("Item description", itemDtos.get(0).getDescription());
        assertEquals(true, itemDtos.get(0).getAvailable());
        assertNull(itemDtos.get(0).getRequestId());
    }

    @Test
    void searchItemWithDescriptionInUpperFirstLetterTest() {
        when(repository.findByNameOrDescriptionLike(anyString()))
                .thenReturn(List.of(item));

        List<ItemDto> itemDtos = itemService.searchItem("DESCRIPTION");

        assertEquals(1, itemDtos.size());
        assertEquals(1, itemDtos.get(0).getId());
        assertEquals("Item name", itemDtos.get(0).getName());
        assertEquals("Item description", itemDtos.get(0).getDescription());
        assertEquals(true, itemDtos.get(0).getAvailable());
        assertNull(itemDtos.get(0).getRequestId());
    }

    @Test
    void createItemTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        when(repository.save(any(Item.class)))
                .thenReturn(item);

        ItemDto itemDto = itemService.createItem(item.getId(), itemMapper.toModelDto(item));

        assertEquals(1, itemDto.getId());
        assertEquals("Item name", itemDto.getName());
        assertEquals("Item description", itemDto.getDescription());
        assertEquals(true, itemDto.getAvailable());
        assertNull(itemDto.getRequestId());
    }

    @Test
    void createInappropriateItemWithNoUserTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                itemService.createItem(
                        user1.getId(),
                        itemMapper.toModelDto(item)
                ));
        assertThat(exception.getMessage(), is("Пользователь не найден"));
    }

    @Test
    void createItemWithNoRequestIdTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        ItemDto itemDto = itemMapper.toModelDto(item);
        itemDto.setRequestId(5L);

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                itemService.createItem(
                        user1.getId(),
                        itemDto
                ));
        assertThat(exception.getMessage(), is("Запрос не найден"));
    }

    @Test
    void createItemWithNullItemRequestTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        ItemDto itemDto = itemMapper.toModelDto(item);
        itemDto.setRequestId(3000L);

        NotFoundException exc = assertThrows(NotFoundException.class, () ->
                itemService.createItem(user1.getId(), itemDto)
        );
    }

    @Test
    void updateTest() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(repository.save(any(Item.class)))
                .thenReturn(item);

        ItemDto itemDto = itemService.updateItem(item.getId(), user1.getId(), itemMapper.toModelDto(item));

        assertEquals(1, itemDto.getId());
        assertEquals("Item name", itemDto.getName());
        assertEquals("Item description", itemDto.getDescription());
        assertEquals(true, itemDto.getAvailable());
        assertNull(itemDto.getRequestId());
    }

    @Test
    void updateItemFromNotOwnerTest() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(repository.save(any(Item.class)))
                .thenReturn(item);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(
                        50L,
                        user2.getId(),
                        itemMapper.toModelDto(item)));
    }

    @Test
    void updateItemFromNotUserTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException invalidUserIdException;

        invalidUserIdException = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.getItemById(3L, item.getId()));
        assertThat(invalidUserIdException.getMessage(), is("Вещь не найдена"));
    }

    @Test
    void updateItemFromNotItemTest() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException invalidItemIdException;

        invalidItemIdException = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.getItemById(2L, item.getId()));
        assertThat(invalidItemIdException.getMessage(), is("Вещь не найдена"));
    }

    @Test
    void addCommentTest() {
        when(bookingRepository.findByBookerIdAndItemIdAndEndBefore(
                anyLong(),
                anyLong(),
                any(LocalDateTime.class)))
                .thenReturn(Optional.ofNullable(booking));

        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        CommentDto commentDto = itemService
                .addComment(1L, 1L, commentMapper.toModelDto(comment));

        assertEquals(1, commentDto.getId());
        assertEquals("Comment text", commentDto.getText());
        assertEquals("User2 name", commentDto.getAuthorName());
    }

    @Test
    void createCommentTest() {
        when(bookingRepository.findByBookerIdAndItemIdAndEndBefore(
                anyLong(),
                anyLong(),
                any(LocalDateTime.class)))
                .thenReturn(Optional.of(booking));

        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        CommentDto commentDto = itemService.addComment(
                1,
                1,
                commentMapper.toModelDto(comment)
        );

        assertEquals(1, commentDto.getId());
        assertEquals("Comment text", commentDto.getText());
        assertEquals("User2 name", commentDto.getAuthorName());
    }

    @Test
    void createCommentFromUserWithoutBookingTest() {
        when(bookingRepository.findByBookerIdAndItemIdAndEndBefore(
                anyLong(),
                anyLong(),
                any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.addComment(
                        1,
                        1,
                        commentMapper.toModelDto(comment)
                ));

        assertEquals(
                "Пользователь не найден",
                exception.getMessage());
    }
}