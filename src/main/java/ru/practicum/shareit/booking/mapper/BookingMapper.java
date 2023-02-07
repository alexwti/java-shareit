package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class BookingMapper {
    public BookingDto toModelDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus(),
                booking.getStart(),
                booking.getEnd());
    }

    public Booking toModel(BookingDto bookingDto, Item item, User user) {
        return new Booking(bookingDto.getId(),
                item,
                user,
                bookingDto.getStatus(),
                bookingDto.getStart(),
                bookingDto.getEnd());
    }
}
