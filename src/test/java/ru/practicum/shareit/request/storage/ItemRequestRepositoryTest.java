package ru.practicum.shareit.request.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();
        user1 = new User(null, "User1 name", "user1@yandex.ru");
        user2 = new User(null, "User2 name", "user2@yandex.ru");
        em.persist(user1);
        em.persist(user2);

        itemRequest1 = ItemRequest.builder()
                .id(null)
                .description("ItemRequest1 description")
                .requester(user1)
                .created(now)
                .build();
        em.persist(itemRequest1);
        itemRequest2 = ItemRequest.builder()
                .id(null)
                .description("ItemRequest1 description")
                .requester(user2)
                .created(now)
                .build();
        em.persist(itemRequest2);
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    void findAllByRequesterIdOrderByCreatedDescTest() {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequester_IdOrderByCreatedDesc(user1.getId());

        assertEquals(List.of(itemRequest1), itemRequests);
    }

    @Test
    void findAllByRequesterIdIsNotOrderByCreatedDescTest() {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequester_IdIsNotOrderByCreatedDesc(user1.getId(), PageRequest.of(0 / 10, 10));

        assertEquals(List.of(itemRequest2), itemRequests);
    }
}