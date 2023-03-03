package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.model.User;

@RestController
public interface UserRepository extends JpaRepository<User, Long> {
}
