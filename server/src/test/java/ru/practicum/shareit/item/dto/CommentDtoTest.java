package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {

    CommentMapper commentMapper = new CommentMapper();
    @Autowired
    private JacksonTester<CommentDto> json;
    private CommentDto commentDto;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();

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

        Comment comment = Comment.builder()
                .id(1L)
                .text("Comment text")
                .item(item)
                .author(user2)
                .created(now)
                .build();
        commentDto = commentMapper.toModelDto(comment);
    }

    @Test
    void testSerialize() throws Exception {
        JsonContent<CommentDto> result = json.write(commentDto);
        Integer commentId1 = Math.toIntExact(commentDto.getId());

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.text");
        assertThat(result).hasJsonPath("$.authorName");
        assertThat(result).hasJsonPath("$.created");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(commentId1);
        assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo(commentDto.getText());
        assertThat(result).extractingJsonPathStringValue("$.authorName")
                .isEqualTo(commentDto.getAuthorName());
    }
}