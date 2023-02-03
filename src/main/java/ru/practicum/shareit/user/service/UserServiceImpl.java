package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DataExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository repository;

    @Transactional(readOnly = true)
    @Override
    public List<User> findAll() {
        log.info("Выгружен список пользователей");
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public User getUserById(long id) {
        log.info("Пользователь с id {} выгружен", id);
        return repository.findById(id).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", id);
            throw new NotFoundException("Пользователь не найден");
        });
    }

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        //emailValidate(userDto.getEmail());
        try {
            User user = repository.save(userMapper.toModel(userDto));
            log.info("Пользователь {} создан", user);
            return userMapper.toModelDto(user);
        } catch (DataExistException e) {
            throw new DataExistException(String.format("Пользователь с email %s уже есть в базе", userDto.getEmail()));
        }
    }

    @Transactional
    @Override
    public User updateUser(long id, User user) {
        User updUser = repository.findById(id).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", id);
            throw new NotFoundException("Пользователь не найден");
        });
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            try {
                updUser.setEmail(user.getEmail());
            } catch (DataExistException e) {
                throw new DataExistException(String.format("Пользователь с email %s уже есть в базе", user.getEmail()));
            }
        }
        if (user.getName() != null && !user.getName().trim().isEmpty()) {
            updUser.setName(user.getName());
        }
        updUser = repository.save(updUser);
        log.info("Пользователь с id {} обновлен", id);
        return updUser;
    }

    @Transactional
    @Override
    public void deleteUser(long id) {
        log.info("Пользователь с id {} удалён", id);
        repository.deleteById(id);
    }
}
