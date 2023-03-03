package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingClient bookingClient;


    BookingDto createBookingDto() {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(LocalDateTime.now().plusDays(2));
        dto.setEnd(LocalDateTime.now().plusDays(3));
        return dto;
    }

    BookingDto getBookingDto() {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStatus(BookingStatus.WAITING);
        return dto;
    }

    @Test
    void createBookingTest() throws Exception {
        BookingDto createBookingDto = createBookingDto();
        createBookingDto.setItemId(1L);
        createBookingDto.setBookerId(1L);
        BookingDto savedBookingDto = getBookingDto();
        savedBookingDto.setStart(createBookingDto.getStart());
        savedBookingDto.setEnd(createBookingDto.getEnd());
        savedBookingDto.setItemId(createBookingDto.getItemId());
        savedBookingDto.setStatus(createBookingDto.getStatus());
        Long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.status(201).body(savedBookingDto);
        when(bookingClient.createBooking(anyLong(), any(BookingDto.class))).thenReturn(response);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(createBookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(savedBookingDto)));
    }

    @Test
    void createBookingStartAfterEndTest() throws Exception {
        BookingDto createBookingDto = createBookingDto();
        createBookingDto.setStart(LocalDateTime.now().plusDays(5));
        createBookingDto.setEnd(LocalDateTime.now().plusDays(2));
        Long userId = 1L;

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(createBookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changeBookingStatusTest() throws Exception {
        long bookingId = 1L;
        long ownerId = 1L;
        BookingDto dto = getBookingDto();
        dto.setStatus(BookingStatus.APPROVED);
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dto);
        when(bookingClient.changeBookingStatus(bookingId, ownerId, true)).thenReturn(response);

        mockMvc.perform(patch("/bookings/{id}", bookingId)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    void getBookingsByBookerTest() throws Exception {
        int from = 0;
        int size = 10;
        long userId = 1L;
        List<BookingDto> dtoList = List.of(getBookingDto());
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dtoList);
        when(bookingClient.getBookingsByBooker(userId, "ALL", from, size))
                .thenReturn(response);

        mockMvc.perform(get("/bookings/")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoList)));
    }

    @Test
    void getBookingsByOwnerTest() throws Exception {
        Integer from = 0;
        Integer size = 10;
        long ownerId = 1L;
        List<BookingDto> dtoList = List.of(getBookingDto());
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dtoList);
        when(bookingClient.getBookingsByOwner(ownerId, "ALL", from, size))
                .thenReturn(response);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", "ALL")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoList)));
    }

    @Test
    void getBookingInfoTest() throws Exception {
        long bookingId = 1L;
        long userId = 1L;
        BookingDto dto = getBookingDto();
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dto);
        when(bookingClient.getBookingInfo(bookingId, userId)).thenReturn(response);

        mockMvc.perform(get("/bookings/{id}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }
}