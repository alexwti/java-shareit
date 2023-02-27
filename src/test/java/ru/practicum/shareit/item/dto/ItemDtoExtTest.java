package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoExtTest {

    @Autowired
    private JacksonTester<ItemDtoExt> json;

    private ItemDtoExt itemDtoExt;
    private ItemMapper itemMapper = new ItemMapper();
    private BookingMapper bookingMapper = new BookingMapper();
    private CommentMapper commentMapper = new CommentMapper();

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        User user1 = new User(1L, "User1 name", "user1@yandex.ru");
        User user2 = new User(2L, "User2 name", "user2@yandex.ru");

        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("ItemRequest description")
                .requester(user1)
                .created(now)
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Item name")
                .description("Item description")
                .available(true)
                .ownerId(user1.getId())
                .request(null)
                .build();
        itemDtoExt = itemMapper.toModelDtoExt(item);

        Booking booking1 = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .build();
        BookingDto booking1Dto = bookingMapper.toModelDto(booking1);

        Booking booking2 = Booking.builder()
                .id(1L)
                .start(start.plusDays(1))
                .end(end.plusDays(1))
                .item(item)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .build();
        BookingDto booking2Dto = bookingMapper.toModelDto(booking2);
        itemDtoExt.setLastBooking(booking1Dto);
        itemDtoExt.setNextBooking(booking2Dto);

        Comment comment = Comment.builder()
                .id(1L)
                .text("Comment text")
                .item(item)
                .author(user2)
                .created(now)
                .build();
        CommentDto commentDto = commentMapper.toModelDto(comment);
        itemDtoExt.setComments(List.of(commentDto));
    }

    @Test
    void testSerialize() throws Exception {
        JsonContent<ItemDtoExt> result = json.write(itemDtoExt);

        Integer value = Math.toIntExact(itemDtoExt.getId());
        Integer lasBookingId = Math.toIntExact(itemDtoExt.getLastBooking().getId());
        Integer nextBookingId = Math.toIntExact(itemDtoExt.getNextBooking().getId());

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.lastBooking");
        assertThat(result).hasJsonPath("$.nextBooking");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(value);
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemDtoExt.getName());
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemDtoExt.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemDtoExt.getAvailable());
        assertThat(result).extractingJsonPathNumberValue(
                "$.lastBooking.id").isEqualTo(lasBookingId);
        assertThat(result).extractingJsonPathNumberValue(
                "$.nextBooking.id").isEqualTo(nextBookingId);

    }
}