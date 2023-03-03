package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static ru.practicum.shareit.item.controller.ItemController.sharerUserId;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(sharerUserId) long userId,
                                                @Valid @RequestBody BookingDto bookingDto) {
        return bookingClient.createBooking(userId, bookingDto);

    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> changeBookingStatus(@RequestHeader(sharerUserId) long userId,
                                                      @PathVariable long bookingId,
                                                      @RequestParam boolean approved) {
        return bookingClient.changeBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingInfo(@RequestHeader(sharerUserId) long userId,
                                                 @PathVariable long bookingId) {
        return bookingClient.getBookingInfo(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByBooker(@RequestHeader(sharerUserId) long bookerId,
                                                      @RequestParam(defaultValue = "ALL", required = false) String state,
                                                      @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
                                                      @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) int size) {
        return bookingClient.getBookingsByBooker(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(@RequestHeader(sharerUserId) long ownerId,
                                                     @RequestParam(defaultValue = "ALL", required = false) String state,
                                                     @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
                                                     @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) int size) {
        return bookingClient.getBookingsByOwner(ownerId, state, from, size);
    }
}
