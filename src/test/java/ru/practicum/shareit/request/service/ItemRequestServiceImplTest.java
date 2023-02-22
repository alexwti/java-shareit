package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ItemRequestServiceImplTest {

    private final ItemRequestMapper itemRequestMapper = new ItemRequestMapper();
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    private ItemRequestDto itemRequest1Dto;
    private Item item1;
    private User user1;
    private User user2;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;


    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();
        user1 = new User(1L, "User1 name", "user1@yandex.ru");
        user2 = new User(2L, "User2 name", "user2@yandex.ru");

        itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("ItemRequest1 description")
                .requester(user1)
                .created(now)
                .build();
        itemRequest1Dto = itemRequestMapper.toModelDto(itemRequest1);

        itemRequest2 = ItemRequest.builder()
                .id(2L)
                .description("ItemRequest2 description")
                .requester(user2)
                .created(now)
                .build();

        item1 = Item.builder()
                .id(1L)
                .name("Item1 name")
                .description("Item1 description")
                .available(true)
                .ownerId(user1.getId())
                .request(itemRequest1)
                .build();

    }

    @Test
    void createItemRequestWhenUserFoundTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user1));

        when(requestRepository.save(any())).thenReturn(itemRequest1);

        ItemRequestDto itemRequestDto = itemRequestService.createItemRequest(itemRequest1Dto, user1.getId());

        itemRequest1Dto.setCreated(itemRequestDto.getCreated());

        assertEquals(itemRequest1Dto.getId(), itemRequestDto.getId());
        verify(requestRepository, Mockito.times(1)).save(any());
    }

    @Test
    void createItemRequestWhenUserNotFoundTest() {
        when(userRepository.findById(anyLong())).thenThrow(new NotFoundException("User not found"));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemRequestService.createItemRequest(itemRequest1Dto, 3L));
        assertEquals("User not found", exception.getMessage());
    }


    @Test
    void getForUserRequestsWhenUserFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user1));

        List<ItemRequestDto> responseList = itemRequestService.getForUserRequests(user1.getId());
        assertEquals(0, responseList.size());
        verify(requestRepository).findAllByRequesterIdOrderByCreatedDesc(anyLong());
    }

    @Test
    void getForUserRequestsWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenThrow(new NotFoundException("User not found"));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemRequestService.getForUserRequests(3L));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getForNotForUserRequestsWhenUserFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(user2));

        List<ItemRequestDto> responseList = itemRequestService.getNotForUserRequests(user1.getId(), 0, 10);
        assertEquals(0, responseList.size());
        verify(requestRepository).findAllByRequesterIdIsNotOrderByCreatedDesc(anyLong(), any(PageRequest.class));
    }

    @Test
    void getForNotForUserRequestsWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenThrow(new NotFoundException("User not found"));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemRequestService.getNotForUserRequests(3L, 0, 10));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getItemRequestWhenAllFoundTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user1));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest1));

        when(itemRepository.findAllByRequest_Id(anyLong())).thenReturn(Collections.singletonList(item1));

        ItemRequestDto responseRequest = itemRequestService.getItemRequest(user1.getId(), itemRequest1Dto.getId());

        assertEquals(1, responseRequest.getId());
        assertEquals("ItemRequest1 description", responseRequest.getDescription());
        assertEquals(user1.getId(), responseRequest.getRequesterid());


        assertNotNull(responseRequest);
        verify(requestRepository).findById(anyLong());
        verify(itemRepository).findAllByRequest_Id(anyLong());
    }

    @Test
    void getItemRequestWhenRequestNotFoundFoundTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user1));
        when(requestRepository.findById(anyLong())).thenThrow(new NotFoundException("Request not found"));

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                itemRequestService.getItemRequest(3L, itemRequest1Dto.getId()));
        assertEquals("Request not found", exception.getMessage());
    }

    @Test
    void getItemRequestWhenUserNotFoundFoundTest() {
        when(userRepository.findById(anyLong())).thenThrow(new NotFoundException("User not found"));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest1));

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                itemRequestService.getItemRequest(3L, itemRequest1Dto.getId()));
        assertEquals("User not found", exception.getMessage());
    }
}