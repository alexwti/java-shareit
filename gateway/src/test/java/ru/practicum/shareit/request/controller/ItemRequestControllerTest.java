package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.client.RequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private RequestClient requestClient;

    ItemRequestDto getRequestDto() {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("TestDescription");
        dto.setCreated(LocalDateTime.now());
        return dto;
    }

    @Test
    void createItemRequestTest() throws Exception {
        long userId = 1L;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("TestDescription");
        ItemRequestDto savedDto = getRequestDto();
        ResponseEntity<Object> response = ResponseEntity.status(200).body(savedDto);
        when(requestClient.createItemRequest(anyLong(), any(ItemRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(savedDto)));
    }

    @Test
    void getItemRequestTest() throws Exception {
        long userId = 1L;
        long requestId = 1L;
        ItemRequestDto dto = getRequestDto();
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dto);
        when(requestClient.getItemRequest(userId, requestId)).thenReturn(response);

        mockMvc.perform(get("/requests/{id}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    void getForUserRequestsTest() throws Exception {
        long userId = 1L;
        List<ItemRequestDto> dtoList = List.of(getRequestDto());
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dtoList);
        when(requestClient.getForUserRequests(userId)).thenReturn(response);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoList)));
    }

    @Test
    void getNotForUserRequests() throws Exception {
        long userId = 1L;
        int from = 0;
        int size = 10;
        List<ItemRequestDto> dtoList = List.of(getRequestDto());
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dtoList);
        when(requestClient.getNotForUserRequests(userId, from, size)).thenReturn(response);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoList)));
    }
}