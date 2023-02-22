package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserServiceImpl userService;

    private UserDto user1Dto;
    private User user1;
    private UserMapper userMapper = new UserMapper();
    public static final String sharerUserId = "X-Sharer-User-Id";

    @BeforeEach
    void beforeEach() {
        user1 = new User(1L, "User1 name", "user1@yandex.ru");
        user1Dto = userMapper.toModelDto(user1);
    }

    @Test
    void createUserTest() throws Exception {
        when(userService.createUser(any(UserDto.class)))
                .thenReturn(user1Dto);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user1Dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(user1Dto)));
    }

    @Test
    void updateUserTest() throws Exception {
        when(userService.updateUser(anyLong(), any(User.class)))
                .thenReturn(user1);

        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(user1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(user1)));
    }

    @Test
    void findAllTest() throws Exception {
        when(userService.findAll())
                .thenReturn(List.of(user1));

        mockMvc.perform(get("/users/"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(user1))));
    }

    @Test
    void getUserByIdTest() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user1);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(user1)));
    }
    @Test
    void deleteUserTest() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user1);

        mockMvc.perform(delete("/users/1")
                        .header(sharerUserId, 1L))
                .andExpect(status().isOk());
    }
}