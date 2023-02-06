package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.model.BookingStatus;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoShortC {

    private long id;

    @NotNull
    private Long itemId;

    private Long bookerId;

    private BookingStatus status;

    @FutureOrPresent
    private LocalDateTime start;

    @Future
    private LocalDateTime end;
}
