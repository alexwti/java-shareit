package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class UserStorageImpl implements UserStorage {

    private int id = 1;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        return users.values().stream().collect(Collectors.toList());
    }

    @Override
    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        emailValidate(userDto.getEmail());
        userDto.setId(id++);
        users.put(userDto.getId(), UserMapper.toUser(userDto));
        return userDto;
    }

    @Override
    public User updateUser(long id, User user) {
        if (users.containsKey(id)) {
            if (user.getEmail() != null) {
                emailValidate(user.getEmail());
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
            UserDto userDto = UserMapper.toUserDto(users.get(id));
            users.remove(id);
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private void emailValidate(String email) {
        List<User> users = findAll();
        if (users.stream().anyMatch(u -> u.getEmail().equals(email))) {
            throw new ValidationException("Пользователь с таким e-mail уже существует");
        }
    }

}
