package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    UserDto getTestUserDto() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("Usrer1");
        dto.setEmail("user1@yandex.ru");
        return dto;
    }

    @Test
    void createUserTest() throws Exception {
        UserDto dto = getTestUserDto();
        ResponseEntity<Object> response = ResponseEntity.status(201).body(dto);
        when(userClient.createUser(any(UserDto.class))).thenReturn(response);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    void updateUserTest() throws Exception {
        UserDto dto = getTestUserDto();
        long userId = 1L;

        ResponseEntity<Object> response = ResponseEntity.status(200).body(dto);
        when(userClient.updateUser(anyLong(), any(UserDto.class))).thenReturn(response);

        mockMvc.perform(patch("/users/{id}", userId)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    void getUserByIdTest() throws Exception {
        UserDto dto = getTestUserDto();
        long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dto);
        when(userClient.getUserById(userId)).thenReturn(response);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    void getUserByIdUserNotFoundTest() throws Exception {
        long userId = 0L;
        String errorMessage = "User with id " + userId + " not found";
        ResponseEntity<Object> response = ResponseEntity.status(404).body(new ErrorResponse(errorMessage));
        when(userClient.getUserById(userId)).thenReturn(response);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(errorMessage)));
    }

    @Test
    void findAllTest() throws Exception {
        List<UserDto> dtoList = List.of(getTestUserDto());
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dtoList);
        when(userClient.findAll()).thenReturn(response);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoList)));
    }

    @Test
    void deleteUserTest() throws Exception {
        long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.status(200).build();

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk());
    }

    @Getter
    @RequiredArgsConstructor
    static class ErrorResponse {
        private final String error;
    }
}