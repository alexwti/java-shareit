package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookingDto {
    @NotNull
    private Long id;

    @NotNull
    private Item item;

    private User booker;

    @NotNull
    private BookingStatus status;

    @FutureOrPresent
    private LocalDateTime start;

    @Future
    private LocalDateTime end;
}
