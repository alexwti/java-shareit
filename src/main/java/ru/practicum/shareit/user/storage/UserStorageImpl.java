package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserStorageImpl implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final UserMapperImpl userMapper = new UserMapperImpl();
    private int id = 1;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setId(id++);
        users.put(userDto.getId(), userMapper.toModel(userDto));
        return userDto;
    }

    @Override
    public User updateUser(long id, User user) {
        if (users.containsKey(id)) {
            if (user.getEmail() != null) {
                users.get(id).setEmail(user.getEmail());
            }
            if (user.getName() != null) {
                users.get(id).setName(user.getName());
            }
            return users.get(id);
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    @Override
    public void deleteUser(long id) {
        if (users.containsKey(id)) {
            users.remove(id);
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }
}
