package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestControllerTest {

    public static final String SharerUserId = "X-Sharer-User-Id";
    @Autowired
    private final ObjectMapper objectMapper;
    private final ItemRequestMapper itemRequestMapper = new ItemRequestMapper();
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mvc;
    private ItemRequestDto itemRequest1Dto;
    private ItemRequestDto itemRequest2Dto;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();

        User user1 = new User(1L, "User1 name", "user1@yandex.ru");
        User user2 = new User(2L, "User2 name", "user2@yandex.ru");

        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("ItemRequest1 description")
                .requester(user1)
                .created(now)
                .build();
        itemRequest1Dto = itemRequestMapper.toModelDto(itemRequest1);

        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(2L)
                .description("ItemRequest2 description")
                .requester(user2)
                .created(now)
                .build();
        itemRequest2Dto = itemRequestMapper.toModelDto(itemRequest2);
    }

    @Test
    void createItemRequestTest() throws Exception {
        when(itemRequestService.createItemRequest(any(), anyLong())).thenReturn(itemRequest1Dto);

        mvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SharerUserId, 1L)
                        .content(objectMapper.writeValueAsString(itemRequest1Dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.requesterid").value(1L))
                .andExpect(jsonPath("$.description").value("ItemRequest1 description"));
    }

    @Test
    void getForUserRequestsTest() throws Exception {
        when(itemRequestService.getForUserRequests(anyLong())).thenReturn(Collections.singletonList(itemRequest1Dto));

        mvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SharerUserId, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].requesterid").value(1L))
                .andExpect(jsonPath("$[0].description").value("ItemRequest1 description"));
    }

    @Test
    void getNotForUserRequestsTest() throws Exception {
        when(itemRequestService.getNotForUserRequests(anyLong(), anyInt(), anyInt())).thenReturn(Collections.singletonList(itemRequest2Dto));

        mvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SharerUserId, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].requesterid").value(2L))
                .andExpect(jsonPath("$[0].description").value("ItemRequest2 description"));
    }

    @Test
    void getItemRequestTest() throws Exception {
        when(itemRequestService.getItemRequest(anyLong(), anyLong())).thenReturn(itemRequest1Dto);

        mvc.perform(get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SharerUserId, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.requesterid").value(1L))
                .andExpect(jsonPath("$.description").value("ItemRequest1 description"));
    }
}