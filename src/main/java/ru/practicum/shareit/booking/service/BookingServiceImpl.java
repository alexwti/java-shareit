package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper = new BookingMapper();

    @Override
    @Transactional
    public BookingDto createBooking(long bookerId, BookingDto bookingDto) {
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> {
            log.warn("Вещь id {} не найдена", itemRepository.findById(bookingDto.getItemId()));
            throw new NotFoundException("Вещь не найдена");
        });
        User user = userRepository.findById(bookerId).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", bookerId);
            throw new NotFoundException("Пользователь не найден");
        });
        if (item.getOwnerId() == bookerId) {
            log.warn("Пользователь с id {} с владельцем вещи", bookerId);
            throw new NotFoundException("Вы не можете заказать вещь сами у себя");
        }
        if (!item.getAvailable()) {
            log.warn("Вещь с id {} в статусе недоступности для заказа", item.getId());
            throw new BadRequestException("Вещь недоступна для заказа");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new BadRequestException("Время окончания заказа не может быть раньше начала");
        }
        bookingDto.setStatus(BookingStatus.WAITING);
        log.info("Бронь создана");
        return bookingMapper.toModelDto(bookingRepository.save(bookingMapper.toModel(bookingDto, item, user)));
    }

    @Override
    @Transactional
    public BookingDto changeBookingStatus(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.warn("Резерв id {} не найден", bookingId);
            throw new NotFoundException("Резерв не найден");
        });
        Item item = booking.getItem();
        if (userId != item.getOwnerId()) {
            log.warn("Пользователь {} не имеент прав на подтверждение аренды этой вещи {}", userId, item.getId());
            throw new NotFoundException("У вас нет прав на подтверждение аренды этой вещи");
        }
        if (booking.getStatus() == BookingStatus.APPROVED) {
            log.warn("Резерв {} уже утвержден", bookingId);
            throw new BadRequestException("Вы не можете сменить статус у утвержденного резерва");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingMapper.toModelDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingInfo(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.warn("Резерв id {} не найден", bookingId);
            throw new NotFoundException("Резерв не найден");
        });
        Item item = booking.getItem();
        if (booking.getBooker().getId() == userId || item.getOwnerId() == userId) {
            return bookingMapper.toModelDto(booking);
        } else {
            log.warn("Пользователь id {} не имеет прав работы с резервом {}", userId, bookingId);
            throw new NotFoundException("У вас нет прав на просмотр сведений об аренде этой вещи");
        }
    }

    @Override
    public List<BookingDto> getBookingsByBooker(long bookerId, String state, int from, int size) {
        userRepository.findById(bookerId).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", bookerId);
            throw new NotFoundException("Пользователь не найден");
        });

        Pageable pageable = PageRequest.of(from / size, size);

        List<Booking> bookings = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case "ALL":
                bookings.addAll(bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId, pageable));
                break;
            case "CURRENT":
                bookings.addAll(bookingRepository.findAllByBookerIdCurrent(bookerId, LocalDateTime.now(), pageable));
                break;
            case "PAST":
                bookings.addAll(bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(bookerId, LocalDateTime.now(), pageable));
                break;
            case "FUTURE":
                bookings.addAll(bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(bookerId, LocalDateTime.now(), pageable));
                break;
            case "WAITING":
                bookings.addAll(bookingRepository.findAllByBookerIdAndStatusIsOrderByStartDesc(bookerId, BookingStatus.WAITING, pageable));
                break;
            case "REJECTED":
                bookings.addAll(bookingRepository.findAllByBookerIdAndStatusIsOrderByStartDesc(bookerId, BookingStatus.REJECTED, pageable));
                break;
            default:
                log.warn("Неизестный статус {}", state);
                String message = String.format("Unknown state: %S", state);
                throw new UnsupportedStateException(message);
        }
        return bookings.stream()
                .map(bookingMapper::toModelDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsByOwner(long ownerId, String state, int from, int size) {
        userRepository.findById(ownerId).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", ownerId);
            throw new NotFoundException("Пользователь не найден");
        });

        Pageable pageable = PageRequest.of(from / size, size);

        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case "ALL":
                bookings.addAll(bookingRepository.findAllByOwnerId(ownerId, pageable));
                break;
            case "CURRENT":
                bookings.addAll(bookingRepository.findAllByOwnerIdCurrent(ownerId, LocalDateTime.now(), pageable));
                break;
            case "PAST":
                bookings.addAll(bookingRepository.findAllByOwnerIdAndEndBefore(ownerId, LocalDateTime.now(), pageable));
                break;
            case "FUTURE":
                bookings.addAll(bookingRepository.findAllByOwnerIdAndStartAfter(ownerId, LocalDateTime.now(), pageable));
                break;
            case "WAITING":
                bookings.addAll(bookingRepository.findAllByOwnerIdAndStatus(ownerId, BookingStatus.WAITING, pageable));
                break;
            case "REJECTED":
                bookings.addAll(bookingRepository.findAllByOwnerIdAndStatus(ownerId, BookingStatus.REJECTED, pageable));
                break;
            default:
                log.warn("Неизестный статус {}", state);
                String message = String.format("Unknown state: %S", state);
                throw new UnsupportedStateException(message);
        }
        return bookings.stream()
                .map(bookingMapper::toModelDto)
                .collect(Collectors.toList());
    }
}
