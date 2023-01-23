package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage storage;

    @Override
    public List<User> findAll() {
        log.info("Выгружен список пользователей");
        return storage.findAll();
    }

    @Override
    public User getUserById(long id) {
        log.info("Пользователь с id{} выгружен", id);
        return storage.getUserById(id).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", id);
            throw new NotFoundException("Пользователь не найден");
        });
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        emailValidate(userDto.getEmail());
        UserDto crUserDto = storage.createUser(userDto);
        log.info("Пользователь создан");
        return crUserDto;
    }

    @Override
    public User updateUser(long id, User user) {
        emailValidate(user.getEmail());
        User updUser = storage.updateUser(id, user);
        log.info("Пользователь с id {} обновлен", id);
        return updUser;
    }

    @Override
    public void deleteUser(long id) {
        log.info("Пользователь с id {} удалён", id);
        storage.deleteUser(id);
    }

    public void emailValidate(String email) {
        List<User> users = findAll();
        if (users.stream().anyMatch(u -> u.getEmail().equals(email))) {
            throw new ValidationException("Пользователь с таким e-mail уже существует");
        }
    }
}
