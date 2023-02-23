package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingServiceImplTest {

    BookingMapper bookingMapper = new BookingMapper();
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    private LocalDateTime start;
    private LocalDateTime end;
    private User user1;
    private User user2;
    private Item item;
    private Booking booking;

    @BeforeEach
    void beforeEach() {

        start = LocalDateTime.now().plusDays(1);
        end = LocalDateTime.now().plusDays(2);

        user1 = new User(1L, "User1 name", "user1@mail.com");
        user2 = new User(2L, "User2 name", "user2@mail.com");

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
    }

    @Test
    void createBookingTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingDto bookingDto = bookingService.createBooking(
                user2.getId(),
                bookingMapper.toModelDto(booking)
        );

        assertEquals(1, bookingDto.getId());
        assertEquals(start, bookingDto.getStart());
        assertEquals(end, bookingDto.getEnd());
        assertEquals(item, bookingDto.getItem());
        assertEquals(user2, bookingDto.getBooker());
    }

    @Test
    void createBookingWithBookerAsOwnerUserTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(user1.getId(),
                        bookingMapper.toModelDto(booking)
                ));

        assertEquals("Вы не можете заказать вещь сами у себя", exception.getMessage());
    }

    @Test
    void createBookingOnNotAvailableItemTest() {
        item.setAvailable(false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.createBooking(user2.getId(),
                        bookingMapper.toModelDto(booking)));

        assertEquals("Вещь недоступна для заказа", exception.getMessage());
    }

    @Test
    void createBookingWithWrongStartTest() {
        booking.setEnd(LocalDateTime.now().minusDays(3));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.createBooking(user2.getId(),
                        bookingMapper.toModelDto(booking)));

        assertEquals("Время окончания заказа не может быть раньше начала", exception.getMessage());
    }

    @Test
    void createBookingOnNotExistingItemTest() {
        booking.setStart(LocalDateTime.now().minusDays(3));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(user1.getId(),
                        bookingMapper.toModelDto(booking)));

        assertEquals("Вещь не найдена", exception.getMessage());
    }

    @Test
    void createBookingWithWrongUserTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(1L,
                        bookingMapper.toModelDto(booking)));

        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void changeBookingStatusTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingDto bookingDto = bookingService.changeBookingStatus(
                user1.getId(),
                booking.getId(),
                true);

        assertEquals(1, bookingDto.getId());
        assertEquals(start, bookingDto.getStart());
        assertEquals(end, bookingDto.getEnd());
        assertEquals(item, bookingDto.getItem());
        assertEquals(user2, bookingDto.getBooker());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void updateBookingWithWrongIdTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.changeBookingStatus(
                        user2.getId(),
                        booking.getId(),
                        true));

        assertEquals("Резерв не найден", exception.getMessage());
    }

    @Test
    void updateBookingFromWrongUserTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.changeBookingStatus(
                        user2.getId(),
                        booking.getId(),
                        true));

        assertEquals("У вас нет прав на подтверждение аренды этой вещи", exception.getMessage());
    }

    @Test
    void changeStatusBookingStatusApprovedTwiceTest() {

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        booking.setStatus(BookingStatus.APPROVED);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.changeBookingStatus(
                        user1.getId(),
                        booking.getId(),
                        true));

        assertEquals("Вы не можете сменить статус у утвержденного резерва", exception.getMessage());
    }

    @Test
    void updateBookingRejectTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingDto bookingDto = bookingService.changeBookingStatus(
                user1.getId(),
                booking.getId(),
                false);

        assertEquals(1, bookingDto.getId());
        assertEquals(start, bookingDto.getStart());
        assertEquals(end, bookingDto.getEnd());
        assertEquals(item, bookingDto.getItem());
        assertEquals(user2, bookingDto.getBooker());
        assertEquals(BookingStatus.REJECTED, bookingDto.getStatus());
    }

    @Test
    void getBookingTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        BookingDto bookingDto = bookingService.getBookingInfo(
                user1.getId(),
                booking.getId());

        assertEquals(1, bookingDto.getId());
        assertEquals(start, bookingDto.getStart());
        assertEquals(end, bookingDto.getEnd());
        assertEquals(item, bookingDto.getItem());
        assertEquals(user2, bookingDto.getBooker());
        assertEquals(BookingStatus.WAITING, bookingDto.getStatus());
    }

    @Test
    void getBookingForBookerTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        booking.setBooker(user1);

        BookingDto bookingDto = bookingService.getBookingInfo(
                user1.getId(),
                booking.getId());

        assertEquals(1, bookingDto.getId());
        assertEquals(start, bookingDto.getStart());
        assertEquals(end, bookingDto.getEnd());
        assertEquals(item, bookingDto.getItem());
        assertEquals(user1, bookingDto.getBooker());
        assertEquals(BookingStatus.WAITING, bookingDto.getStatus());
    }


    @Test
    void getBookingInfoBookingNotFound() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingInfo(
                        user1.getId(),
                        booking.getId()));

        assertEquals("Резерв не найден", exception.getMessage());
    }

    @Test
    void getBookingInfoYouNotABooker() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        booking.setBooker(user2);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingInfo(
                        5L,
                        booking.getId()));

        assertEquals("У вас нет прав на просмотр сведений об аренде этой вещи", exception.getMessage());
    }

    @Test
    void getBookingsTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> bookingDtoResponses = bookingService.getBookingsByBooker(user1.getId(),
                "ALL",
                0,
                10);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getBookingsCurrentStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByBookerIdCurrent(
                anyLong(),
                any(LocalDateTime.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> bookingDtoResponses = bookingService.getBookingsByBooker(user1.getId(),
                "CURRENT",
                0,
                10);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getBookingsAfterStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(
                anyLong(),
                any(LocalDateTime.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> bookingDto = bookingService.getBookingsByBooker(user1.getId(),
                "PAST",
                0,
                10);

        assertEquals(1, bookingDto.size());
        assertEquals(1, bookingDto.get(0).getId());
        assertEquals(start, bookingDto.get(0).getStart());
        assertEquals(end, bookingDto.get(0).getEnd());
        assertEquals(item, bookingDto.get(0).getItem());
        assertEquals(user2, bookingDto.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDto.get(0).getStatus());
    }

    @Test
    void getBookingsStartIsAfterStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(
                anyLong(),
                any(LocalDateTime.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> bookingDto = bookingService.getBookingsByBooker(user1.getId(),
                "FUTURE",
                0,
                10);

        assertEquals(1, bookingDto.size());
        assertEquals(1, bookingDto.get(0).getId());
        assertEquals(start, bookingDto.get(0).getStart());
        assertEquals(end, bookingDto.get(0).getEnd());
        assertEquals(item, bookingDto.get(0).getItem());
        assertEquals(user2, bookingDto.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDto.get(0).getStatus());
    }

    @Test
    void getBookingsWaitingStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByBookerIdAndStatusIsOrderByStartDesc(
                anyLong(),
                any(BookingStatus.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> bookingDto = bookingService.getBookingsByBooker(user1.getId(),
                "WAITING",
                0,
                10);

        assertEquals(1, bookingDto.size());
        assertEquals(1, bookingDto.get(0).getId());
        assertEquals(start, bookingDto.get(0).getStart());
        assertEquals(end, bookingDto.get(0).getEnd());
        assertEquals(item, bookingDto.get(0).getItem());
        assertEquals(user2, bookingDto.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDto.get(0).getStatus());
    }

    @Test
    void getBookingsRejectedStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByBookerIdAndStatusIsOrderByStartDesc(
                anyLong(),
                any(BookingStatus.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> bookingDto = bookingService.getBookingsByBooker(user1.getId(),
                "REJECTED",
                0,
                10);

        assertEquals(1, bookingDto.size());
        assertEquals(1, bookingDto.get(0).getId());
        assertEquals(start, bookingDto.get(0).getStart());
        assertEquals(end, bookingDto.get(0).getEnd());
        assertEquals(item, bookingDto.get(0).getItem());
        assertEquals(user2, bookingDto.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDto.get(0).getStatus());
    }

    @Test
    void getBookingsUnknownStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        UnsupportedStateException exception = assertThrows(UnsupportedStateException.class,
                () -> bookingService.getBookingsByBooker(user1.getId(),
                        "UNKNOWN",
                        0,
                        10));

        assertEquals("Unknown state: UNKNOWN", exception.getMessage());
    }

    @Test
    void getBookingsWithWrongUserTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        booking.setBooker(user2);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingsByBooker(
                        5L,
                        "WAITING",
                        0,
                        10));
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void getItemsOwnerBookingsTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByOwnerId(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> bookingDto = bookingService.getBookingsByOwner(user1.getId(),
                "ALL",
                0,
                10);

        assertEquals(1, bookingDto.size());
        assertEquals(1, bookingDto.get(0).getId());
        assertEquals(start, bookingDto.get(0).getStart());
        assertEquals(end, bookingDto.get(0).getEnd());
        assertEquals(item, bookingDto.get(0).getItem());
        assertEquals(user2, bookingDto.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDto.get(0).getStatus());
    }

    @Test
    void getItemsOwnerBookingsCurrentStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByOwnerIdCurrent(
                anyLong(),
                any(LocalDateTime.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> bookingDto = bookingService.getBookingsByOwner(user1.getId(),
                "CURRENT",
                0,
                10);

        assertEquals(1, bookingDto.size());
        assertEquals(1, bookingDto.get(0).getId());
        assertEquals(start, bookingDto.get(0).getStart());
        assertEquals(end, bookingDto.get(0).getEnd());
        assertEquals(item, bookingDto.get(0).getItem());
        assertEquals(user2, bookingDto.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDto.get(0).getStatus());
    }

    @Test
    void getItemsOwnerBookingsEndBeforeStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByOwnerIdAndEndBefore(
                anyLong(),
                any(LocalDateTime.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> bookingDto = bookingService.getBookingsByOwner(user1.getId(),
                "PAST",
                0,
                10);

        assertEquals(1, bookingDto.size());
        assertEquals(1, bookingDto.get(0).getId());
        assertEquals(start, bookingDto.get(0).getStart());
        assertEquals(end, bookingDto.get(0).getEnd());
        assertEquals(item, bookingDto.get(0).getItem());
        assertEquals(user2, bookingDto.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDto.get(0).getStatus());
    }

    @Test
    void getItemsOwnerBookingsStartAfterStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByOwnerIdAndStartAfter(
                anyLong(),
                any(LocalDateTime.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> bookingDto = bookingService.getBookingsByOwner(user1.getId(),
                "FUTURE",
                0,
                10);

        assertEquals(1, bookingDto.size());
        assertEquals(1, bookingDto.get(0).getId());
        assertEquals(start, bookingDto.get(0).getStart());
        assertEquals(end, bookingDto.get(0).getEnd());
        assertEquals(item, bookingDto.get(0).getItem());
        assertEquals(user2, bookingDto.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDto.get(0).getStatus());
    }

    @Test
    void getItemsOwnerBookingsWaitingStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByOwnerIdAndStatus(
                anyLong(),
                any(BookingStatus.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> bookingDto = bookingService.getBookingsByOwner(user1.getId(),
                "WAITING",
                0,
                10);

        assertEquals(1, bookingDto.size());
        assertEquals(1, bookingDto.get(0).getId());
        assertEquals(start, bookingDto.get(0).getStart());
        assertEquals(end, bookingDto.get(0).getEnd());
        assertEquals(item, bookingDto.get(0).getItem());
        assertEquals(user2, bookingDto.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDto.get(0).getStatus());
    }

    @Test
    void getItemsOwnerBookingsRejectedStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByOwnerIdAndStatus(
                anyLong(),
                any(BookingStatus.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> bookingDto = bookingService.getBookingsByOwner(user1.getId(),
                "REJECTED",
                0,
                10);

        assertEquals(1, bookingDto.size());
        assertEquals(1, bookingDto.get(0).getId());
        assertEquals(start, bookingDto.get(0).getStart());
        assertEquals(end, bookingDto.get(0).getEnd());
        assertEquals(item, bookingDto.get(0).getItem());
        assertEquals(user2, bookingDto.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDto.get(0).getStatus());
    }

    @Test
    void getItemsOwnerBookingsUnknownStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        UnsupportedStateException exception = assertThrows(UnsupportedStateException.class,
                () -> bookingService.getBookingsByOwner(user1.getId(),
                        "UNKNOWN",
                        0,
                        10));

        assertEquals("Unknown state: UNKNOWN", exception.getMessage());
    }

    @Test
    void getItemsOwnerWithWrongUser() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingsByOwner(user1.getId(),
                        "WAITING",
                        0,
                        10));

        assertEquals("Пользователь не найден", exception.getMessage());
    }
}