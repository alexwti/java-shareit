package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemClient itemClient;


    ItemDto createItemDto() {
        return ItemDto.builder()
                .name("TestName")
                .description("TestDescription")
                .available(Boolean.TRUE)
                .build();
    }

    ItemDto getItemDto() {
        return ItemDto.builder()

                .name("TestName")
                .description("TestDescription")
                .available(Boolean.TRUE)
                .build();
    }

    ItemDto getPatchItemDto() {
        return ItemDto.builder()
                .id(1L)
                .name("UpdatedName")
                .description("UpdatedDescription")
                .available(Boolean.TRUE)
                .build();
    }

    ItemDto getUpdatedItemDto() {
        return ItemDto.builder()
                .id(1L)
                .name("UpdatedName")
                .description("UpdatedDescription")
                .available(Boolean.TRUE)
                .build();
    }

    CommentDto getCommentDto() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test");
        return commentDto;
    }

    @Test
    void createItemTest() throws Exception {
        ItemDto createItemDto = createItemDto();
        long ownerId = 1L;
        ItemDto savedDto = getItemDto();
        ResponseEntity<Object> response = ResponseEntity.status(201).body(savedDto);
        when(itemClient.createItem(anyLong(), any(ItemDto.class))).thenReturn(response);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(createItemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(savedDto)));
    }

    @Test
    void updateItemTest() throws Exception {
        ItemDto patchItemDto = getPatchItemDto();
        ItemDto updatedItemDto = getUpdatedItemDto();
        Long itemId = 1L;
        Long ownerId = 1L;
        patchItemDto.setOwnerId(ownerId);
        updatedItemDto.setOwnerId(ownerId);
        ResponseEntity<Object> response = ResponseEntity.status(200).body(updatedItemDto);
        when(itemClient.updateItem(anyLong(), anyLong(), any(ItemDto.class))).thenReturn(response);

        mockMvc.perform(patch("/items/{id}", itemId)
                        .content(objectMapper.writeValueAsString(patchItemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updatedItemDto)));
    }

    @Test
    void getItemByIdTest() throws Exception {
        long itemId = 1L;
        long userId = 1L;
        ItemDto dto = getItemDto();
        dto.setId(1L);
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dto);
        when(itemClient.getItemById(userId, itemId)).thenReturn(response);

        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    void searchItemTest() throws Exception {
        long userId = 1L;
        String text = "test";
        List<ItemDto> dtoList = List.of(getItemDto());
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dtoList);
        when(itemClient.searchItem(text, userId, 0, 10)).thenReturn(response);

        mockMvc.perform(get("/items/search")
                        .param("text", text)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }

    @Test
    void addCommentTest() throws Exception {
        long itemId = 1L;
        long userId = 1L;
        CommentDto dto = getCommentDto();
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dto);
        when(itemClient.addComment(userId, itemId, dto)).thenReturn(response);

        mockMvc.perform(post("/items/{id}/comment", itemId)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }

    @Getter
    @RequiredArgsConstructor
    static class ErrorResponse {
        private final String error;
    }
}