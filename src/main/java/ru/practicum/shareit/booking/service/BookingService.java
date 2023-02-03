package ru.practicum.shareit.booking.service;


import ru.practicum.shareit.booking.dto.BookingDtoShort;

import java.util.List;

public interface BookingService {
    BookingDtoShort createBooking(long bookerId, BookingDtoShort bookingDtoShort);

    BookingDtoShort changeBookingStatus(long userId, long bookingId, boolean approved);

    BookingDtoShort getBookingInfo(long userId, long bookingId);

    List<BookingDtoShort> getBookingsByBooker(long userId, String state);

    List<BookingDtoShort> getBookingsByOwner(long userId, String state);
}
