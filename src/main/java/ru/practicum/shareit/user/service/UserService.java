package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<User> findAll();

    User getUserById(long id);

    UserDto createUser(UserDto userDto);

    User updateUser(long id, User user);

    void deleteUser(long id);

}
