package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class BookingMapper {
    public BookingDtoShort toModelDtoShort(Booking booking) {
        return new BookingDtoShort(booking.getId(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStatus(),
                booking.getStart(),
                booking.getEnd());
    }

    public BookingDto toModelDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus(),
                booking.getStart(),
                booking.getEnd());
    }

    public Booking toModel(BookingDtoShort bookingDtoShort, Item item, User user) {
        return new Booking(bookingDtoShort.getId(),
                item,
                user,
                bookingDtoShort.getStatus(),
                bookingDtoShort.getStart(),
                bookingDtoShort.getEnd());
    }
}
