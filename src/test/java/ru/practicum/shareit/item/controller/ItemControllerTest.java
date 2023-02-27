package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoExt;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(ItemController.class)
class ItemControllerTest {

    private static final String sharerUserId = "X-Sharer-User-Id";
    UserMapper userMapper = new UserMapper();
    ItemMapper itemMapper = new ItemMapper();
    CommentMapper commentMapper = new CommentMapper();
    @MockBean
    private ItemService itemService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    private UserDto user1Dto;
    private ItemDto itemDto;
    private ItemDtoExt itemDtoExt;
    private CommentDto commentDto;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();

        User user1 = new User(1L, "User1 name", "user1@yandex.ru");
        User user2 = new User(2L, "User2 name", "user2@yandex.ru");
        user1Dto = userMapper.toModelDto(user1);

        Item item = Item.builder()
                .id(1L)
                .name("Item name")
                .description("Item description")
                .available(true)
                .ownerId(user1.getId())
                .request(null)
                .build();

        itemDto = itemMapper.toModelDto(item);
        itemDtoExt = itemMapper.toModelDtoExt(item);

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
    void createItemTest() throws Exception {
        when(itemService.createItem(anyLong(), any(ItemDto.class)))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(sharerUserId, user1Dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDto)));
        verify(itemService, times(1))
                .createItem(anyLong(), any(ItemDto.class));
    }


    @Test
    void updateItemTest() throws Exception {
        when((itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class))))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(sharerUserId, user1Dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDto)));
        verify(itemService, times(1))
                .updateItem(anyLong(), anyLong(), any(ItemDto.class));
    }

    @Test
    void getAllItemsOfOwnerTest() throws Exception {
        when(itemService.getAllItemsOfOwner(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDtoExt));

        mockMvc.perform(get("/items")
                        .header(sharerUserId, user1Dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDtoExt))));
        verify(itemService, times(1))
                .getAllItemsOfOwner(anyLong(), anyInt(), anyInt());
    }

    @Test
    void getItemByIdTest() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(itemDtoExt);

        mockMvc.perform(get("/items/1")
                        .header(sharerUserId, user1Dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDtoExt)));
        verify(itemService, times(1))
                .getItemById(anyLong(), anyLong());
    }

    @Test
    void searchItemTest() throws Exception {
        when(itemService.searchItem(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "Item")
                        .header(sharerUserId, user1Dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto))));
        verify(itemService, times(1))
                .searchItem(anyString(), anyInt(), anyInt());
    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(sharerUserId, user1Dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentDto)));
        verify(itemService, times(1))
                .addComment(anyLong(), anyLong(), any(CommentDto.class));
    }
}