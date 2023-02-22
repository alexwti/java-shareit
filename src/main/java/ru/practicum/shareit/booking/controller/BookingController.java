package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.item.controller.ItemController.SharerUserId;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader(SharerUserId) long userId,
                                    @Valid @RequestBody BookingDto bookingDto) {
        bookingDto = bookingService.createBooking(userId, bookingDto);
        return bookingDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto changeBookingStatus(@RequestHeader(SharerUserId) long userId,
                                          @PathVariable long bookingId,
                                          @RequestParam boolean approved) {
        return bookingService.changeBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingInfo(@RequestHeader(SharerUserId) long userId,
                                     @PathVariable long bookingId) {
        return bookingService.getBookingInfo(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookingsByBooker(@RequestHeader(SharerUserId) long bookerId,
                                                @RequestParam(defaultValue = "ALL", required = false) String state,
                                                @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
                                                @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) int size) {
        return bookingService.getBookingsByBooker(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwner(@RequestHeader(SharerUserId) long ownerId,
                                               @RequestParam(defaultValue = "ALL", required = false) String state,
                                               @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
                                               @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) int size) {
        return bookingService.getBookingsByOwner(ownerId, state, from, size);
    }
}
