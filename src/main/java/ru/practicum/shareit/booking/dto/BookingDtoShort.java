package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookingDtoShort {
    @NotNull
    private Long id;

    @NotNull
    private Long itemId;

    @NotNull
    private Long bookerId;

    @NotNull
    private BookingStatus status;

    @FutureOrPresent
    private LocalDateTime start;

    @Future
    private LocalDateTime end;

}
