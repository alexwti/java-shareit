package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {
    private final ItemRequestMapper itemRequestMapper = new ItemRequestMapper();
    private final ItemMapper itemMapper = new ItemMapper();
    @Autowired
    private JacksonTester<ItemRequestDto> json;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();
        User user1 = new User(1L, "User1 name", "user1@yandex.ru");

        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("ItemRequest1 description")
                .requester(user1)
                .created(now)
                .build();
        itemRequestDto = itemRequestMapper.toModelDto(itemRequest);

        Item item = Item.builder()
                .id(1L)
                .name("Item name")
                .description("Item description")
                .available(true)
                .ownerId(user1.getId())
                .request(itemRequest)
                .build();
        ItemDto itemDto = itemMapper.toModelDto(item);
        itemRequestDto.setItems(List.of(itemDto));
    }

    @Test
    void testSerialize() throws Exception {
        JsonContent<ItemRequestDto> response = json.write(itemRequestDto);

        Integer id = Math.toIntExact(itemRequestDto.getId());
        Integer requestorid = Math.toIntExact(itemRequestDto.getRequesterid());

        assertThat(response).hasJsonPath("$.id");
        assertThat(response).hasJsonPath("$.requesterid");
        assertThat(response).hasJsonPath("$.description");
        assertThat(response).hasJsonPath("$.created");
        assertThat(response).hasJsonPath("$.items");

        assertThat(response).extractingJsonPathNumberValue("$.id").isEqualTo(id);
        assertThat(response).extractingJsonPathNumberValue("$.requesterid").isEqualTo(requestorid);
        assertThat(response).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestDto.getDescription());
        assertThat(response).extractingJsonPathArrayValue("$.items").isNotEmpty();
    }
}