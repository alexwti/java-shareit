package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    List<User> findAll();

    Optional<User> getUserById(long id);

    User createUser(User user);

    User updateUser(long id, User user);

    void deleteUser(long id);
}
