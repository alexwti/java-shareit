package ru.practicum.shareit.booking.service;


import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(long bookerId, BookingDtoShort bookingDtoShort);

    BookingDto changeBookingStatus(long userId, long bookingId, boolean approved);

    BookingDto getBookingInfo(long userId, long bookingId);

    List<BookingDto> getBookingsByBooker(long userId, String state);

    List<BookingDto> getBookingsByOwner(long userId, String state);
}