package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
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
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDtoShort createBooking(long bookerId, BookingDtoShort bookingDtoShort) {
        Item item = itemRepository.findById(bookingDtoShort.getItemId()).orElseThrow(() -> {
            log.warn("Вещь id {} не найдена", itemRepository.findById(bookingDtoShort.getItemId()));
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
            throw new ValidationException("Вещь недоступна для заказа");
        }
        bookingDtoShort.setStatus(BookingStatus.WAITING);
        log.info("Вещь создана");
        return bookingMapper.toModelDtoShort(bookingRepository.save(bookingMapper.toModel(bookingDtoShort, item, user)));
    }

    @Override
    @Transactional
    public BookingDtoShort changeBookingStatus(long userId, long bookingId, boolean approved) {
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
            log.warn("Резерв {} ужк утвержден", bookingId);
            throw new ValidationException("Вы не можете сменить статус у утвержденного резерва");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingMapper.toModelDtoShort(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoShort getBookingInfo(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.warn("Резерв id {} не найден", bookingId);
            throw new NotFoundException("Резерв не найден");
        });
        Item item = booking.getItem();
        if (booking.getBooker().getId() == userId || item.getOwnerId() == userId) {
            return bookingMapper.toModelDtoShort(booking);
        } else {
            log.warn("Пользователь id {} не имеет прав работы с резервом {}", userId, bookingId);
            throw new NotFoundException("У вас нет прав на просмотр сведений обаренде этой вещи");
        }
    }

    @Override
    public List<BookingDtoShort> getBookingsByBooker(long bookerId, String state) {
        userRepository.findById(bookerId).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", bookerId);
            throw new NotFoundException("Пользователь не найден");
        });

        List<Booking> bookings = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case "ALL":
                bookings.addAll(bookingRepository.findAllByBookerId(bookerId, sort));
                break;
            case "CURRENT":
                bookings.addAll(bookingRepository.findAllByBookerIdCurrent(bookerId, LocalDateTime.now(), sort));
                break;
            case "PAST":
                bookings.addAll(bookingRepository.findAllByBookerIdAndEndBefore(bookerId, LocalDateTime.now(), sort));
                break;
            case "FUTURE":
                bookings.addAll(bookingRepository.findAllByBookerIdAndStartAfter(bookerId, LocalDateTime.now(), sort));
                break;
            case "WAITING":
                bookings.addAll(bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingStatus.WAITING, sort));
                break;
            case "REJECTED":
                bookings.addAll(bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingStatus.REJECTED, sort));
                break;
            default:
                log.warn("Неизестный статус {}", state);
                throw new NotFoundException("Неизестный статус: " + state);
        }
        return bookings.stream()
                .map(bookingMapper::toModelDtoShort)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoShort> getBookingsByOwner(long ownerId, String state) {
        userRepository.findById(ownerId).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", ownerId);
            throw new NotFoundException("Пользователь не найден");
        });

        List<Booking> bookings = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case "ALL":
                bookings.addAll(bookingRepository.findAllByOwnerId(ownerId, sort));
                break;
            case "CURRENT":
                bookings.addAll(bookingRepository.findAllByOwnerIdCurrent(ownerId, LocalDateTime.now(), sort));
                break;
            case "PAST":
                bookings.addAll(bookingRepository.findAllByOwnerIdAndEndBefore(ownerId, LocalDateTime.now(), sort));
                break;
            case "FUTURE":
                bookings.addAll(bookingRepository.findAllByOwnerIdAndStartAfter(ownerId, LocalDateTime.now(), sort));
                break;
            case "WAITING":
                bookings.addAll(bookingRepository.findAllByOwnerIdAndStatus(ownerId, BookingStatus.WAITING, sort));
                break;
            case "REJECTED":
                bookings.addAll(bookingRepository.findAllByOwnerIdAndStatus(ownerId, BookingStatus.REJECTED, sort));
                break;
            default:
                log.warn("Неизестный статус {}", state);
                throw new NotFoundException("Неизестный статус: " + state);
        }
        return bookings.stream()
                .map(bookingMapper::toModelDtoShort)
                .collect(Collectors.toList());
    }
}
