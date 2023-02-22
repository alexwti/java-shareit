package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserServiceImpl service;

    private User user1;
    private UserMapper userMapper = new UserMapper();

    @BeforeEach
    void beforeEach() {
        user1 = new User(1L, "User1 name", "user1@mail.com");
    }

    @Test
    void createUserTest() {
        when(repository.save(any(User.class))).thenReturn(user1);
        UserDto userDto1 = userMapper.toModelDto(user1);
        UserDto userDto = service.createUser(userDto1);

        assertEquals(1, userDto.getId());
        assertEquals("User1 name", userDto.getName());
        assertEquals("user1@mail.com", userDto.getEmail());
    }


    @Test
    void updateUserWithEmailFormatTest() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(repository.save(any(User.class)))
                .thenReturn(user1);

        service.updateUser(user1.getId(), user1);

        assertEquals(1, user1.getId());
        assertEquals("User1 name", user1.getName());
        assertEquals("user1@mail.com", user1.getEmail());
    }

    @Test
    void updateUserWithNoUser() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());
        user1.setId(10L);
        NotFoundException exc = assertThrows(NotFoundException.class,
                () -> service.updateUser(1L, user1)
        );

        assertEquals("Пользователь не найден", exc.getMessage());
    }

    @Test
    void getAllUsersWhenUserFoundThenReturnedUser() {

        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        when(repository.save(any(User.class)))
                .thenReturn(user1);

        service.updateUser(user1.getId(), user1);

        assertEquals(1, user1.getId());
        assertEquals("User1 name", user1.getName());
        assertEquals("user1@mail.com", user1.getEmail());
    }

    @Test
    void getAllUsersWhenUserFoundThenUserNotFoundExceptionThrown() {
        long userId = 0L;
        //    User expectedUser = new User();
        when(repository.findById(userId))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getUserById(userId));
    }

    @Test
    void getByIdTest() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        User user = service.getUserById(user1.getId());

        assertEquals(1, user.getId());
        assertEquals("User1 name", user.getName());
        assertEquals("user1@mail.com", user.getEmail());
    }

    @Test
    void getUserWrongIdTest() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getUserById(user1.getId()));

        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void deleteUserTest() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        User user = service.deleteUser(user1.getId());

        assertEquals(1, user.getId());
        assertEquals("User1 name", user.getName());
        assertEquals("user1@mail.com", user.getEmail());
    }

    @Test
    void deleteUserTestWithNoUser() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());
        user1.setId(10L);
        NoSuchElementException exc = assertThrows(NoSuchElementException.class,
                () -> service.deleteUser(1L)
        );

        assertEquals("No value present", exc.getMessage());
    }

    @Test
    void getAllUsersTest() {
        when(repository.findAll())
                .thenReturn(List.of(user1));

        List<User> user = service.findAll();

        assertEquals(1, user.size());
        assertEquals(1, user.get(0).getId());
        assertEquals("User1 name", user.get(0).getName());
        assertEquals("user1@mail.com", user.get(0).getEmail());
    }
}