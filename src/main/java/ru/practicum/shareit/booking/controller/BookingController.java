package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.item.controller.ItemController.SHARER_USER_ID;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader(SHARER_USER_ID) long userId,
                                    @Valid @RequestBody BookingDto bookingDto) {
        bookingDto = bookingService.createBooking(userId, bookingDto);
        return bookingDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto changeBookingStatus(@RequestHeader(SHARER_USER_ID) long userId,
                                          @PathVariable long bookingId,
                                          @RequestParam boolean approved) {
        return bookingService.changeBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingInfo(@RequestHeader(SHARER_USER_ID) long userId,
                                     @PathVariable long bookingId) {
        return bookingService.getBookingInfo(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookingsByBooker(@RequestHeader(SHARER_USER_ID) long bookerId,
                                                @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.getBookingsByBooker(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwner(@RequestHeader(SHARER_USER_ID) long ownerId,
                                               @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.getBookingsByOwner(ownerId, state);
    }
}
