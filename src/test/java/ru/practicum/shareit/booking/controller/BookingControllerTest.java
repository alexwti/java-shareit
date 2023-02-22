package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    public static final String sharerUserId = "X-Sharer-User-Id";
    UserMapper userMapper = new UserMapper();
    BookingMapper bookingMapper = new BookingMapper();
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    private UserDto userDto;
    private BookingDto bookingDto;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusDays(1);
        LocalDateTime end = now.plusDays(2);

        User user1 = new User(1L, "User1 name", "user1@yandex.ru");
        User user2 = new User(2L, "User2 name", "user2@yandex.ru");
        userDto = userMapper.toModelDto(user1);

        Item item = Item.builder()
                .id(1L)
                .name("Item name")
                .description("Item description")
                .available(true)
                .ownerId(user1.getId())
                .request(null)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(user1)
                .status(BookingStatus.WAITING)
                .build();
        bookingDto = bookingMapper.toModelDto(booking);
    }

    @Test
    void createBookingTest() throws Exception {
        when(bookingService.createBooking(anyLong(), any(BookingDto.class)))
                .thenReturn(bookingDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(sharerUserId, userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));

    }

    @Test
    void changeBookingStatusTest() throws Exception {
        when(bookingService.changeBookingStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/1")
                        .param("approved", "true")
                        .header(sharerUserId, userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));


    }

    @Test
    void getBookingInfoTest() throws Exception {
        when(bookingService.getBookingInfo(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/1")
                        .header(sharerUserId, userDto.getId()))

                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));
    }

    @Test
    void getBookingsByBookerTest() throws Exception {
        when(bookingService.getBookingsByBooker(anyLong(), any(String.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header(sharerUserId, userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));
    }

    @Test
    void getBookingsByOwnerTest() throws Exception {
        when(bookingService.getBookingsByOwner(anyLong(), any(String.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header(sharerUserId, userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));
    }
}