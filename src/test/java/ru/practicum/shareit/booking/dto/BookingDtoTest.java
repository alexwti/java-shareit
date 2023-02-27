package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {

    BookingMapper bookingMapper = new BookingMapper();
    @Autowired
    private JacksonTester<BookingDto> json;
    private BookingDto bookingDto;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusDays(1);
        LocalDateTime end = now.plusDays(2);

        User user1 = new User(1L, "User1 name", "user1@yandex.ru");
        User user2 = new User(2L, "User2 name", "user2@yandex.ru");

        Item item = Item.builder()
                .id(1L)
                .name("Item name")
                .description("Item description")
                .available(true)
                .ownerId(user1.getId())
                .request(null)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .build();
        bookingDto = bookingMapper.toModelDto(booking);
    }

    @Test
    void testSerialize() throws Exception {
        JsonContent<BookingDto> result = json.write(bookingDto);
        Integer id = Math.toIntExact(bookingDto.getId());
        Integer itemId = Math.toIntExact(bookingDto.getItemId());
        Integer bookerId = Math.toIntExact(bookingDto.getBookerId());

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).hasJsonPath("$.bookerId");
        assertThat(result).hasJsonPath("$.status");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(id);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(itemId);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(bookerId);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(bookingDto.getStatus().toString());
    }
}