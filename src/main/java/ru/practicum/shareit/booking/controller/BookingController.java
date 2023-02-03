package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.item.controller.ItemController.SHARER_USER_ID;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoShort createBooking(@RequestHeader(SHARER_USER_ID) long userId,
                                         @Valid @RequestBody BookingDtoShort bookingDtoShort) {
        return bookingService.createBooking(userId, bookingDtoShort);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoShort changeBookingStatus(@RequestHeader(SHARER_USER_ID) long userId,
                                               @PathVariable long bookingId,
                                               @RequestParam boolean approved) {
        return bookingService.changeBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoShort getBookingInfo(@RequestHeader(SHARER_USER_ID) long userId,
                                          @PathVariable long bookingId) {
        return bookingService.getBookingInfo(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoShort> getBookingsByBooker(@RequestHeader(SHARER_USER_ID) long bookerId,
                                                     @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.getBookingsByBooker(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoShort> getBookingsByOwner(@RequestHeader(SHARER_USER_ID) long ownerId,
                                                    @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.getBookingsByOwner(ownerId, state);
    }
}
