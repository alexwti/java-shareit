package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
public class BookingDto {

    private long id;

    @NotNull
    private Item item;

    private User booker;

    private BookingStatus status;

    @FutureOrPresent
    private LocalDateTime start;

    @Future
    private LocalDateTime end;

    public long getItemId() {
        return this.item.getId();
    }

    @JsonProperty("itemId")
    private void setItemId(long id) {
        item = new Item();
        item.setId(id);
    }

    public long getBookerId() {
        return this.booker.getId();
    }

    @JsonProperty("bookerId")
    private void setBookerId(long id) {
        booker = new User();
        booker.setId(id);
    }
}
