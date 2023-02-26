package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user1;

    private Item item;
    private ItemRequest itemRequest;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();

        user1 = new User(null, "User1 name", "user1@yandex.ru");
        User user2 = new User(null, "User2 name", "user2@yandex.ru");
        em.persist(user1);
        em.persist(user2);

        itemRequest = ItemRequest.builder()
                .id(null)
                .description("ItemRequest description")
                .requester(user1)
                .created(now)
                .build();
        em.persist(itemRequest);

        item = Item.builder()
                .id(null)
                .name("Item name")
                .description("Item description")
                .available(true)
                .ownerId(user1.getId())
                .request(itemRequest)
                .build();
        em.persist(item);
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    void findAllByOwnerIdOrderByIdTest() {
        PageRequest pg = PageRequest.of(0, 10);
        List<Item> items = itemRepository.findAllByOwnerIdOrderById(user1.getId(), pg);
        List<Item> items1 = new ArrayList<>();
        items1.add(item);

        assertEquals(items1.get(0).getId(), items.get(0).getId());
        assertEquals(items1.get(0).getName(), items.get(0).getName());
        assertEquals(items1.get(0).getDescription(), items.get(0).getDescription());
    }

    @Test
    void findByDescriptionLikeTest() {

        String text = "description";
        PageRequest pg = PageRequest.of(0, 10);
        List<Item> items = itemRepository.findByNameOrDescriptionLike(text, pg);

        assertEquals(List.of(item).size(), items.size());
        assertEquals(item.getId(), items.get(0).getId());
        assertEquals(item.getName(), items.get(0).getName());
    }

    @Test
    void findByNameLikeTest() {
        String text = "name";
        PageRequest pg = PageRequest.of(0, 10);
        List<Item> items = itemRepository.findByNameOrDescriptionLike(text, pg);

        assertEquals(List.of(item).size(), items.size());
        assertEquals(item.getId(), items.get(0).getId());
        assertEquals(item.getName(), items.get(0).getName());
    }

    @Test
    void searchByIdTest() {
        Optional<Item> item1 = itemRepository.findById(item.getId());

        assertEquals(item.getId(), item1.get().getId());
        assertEquals(item.getName(), item1.get().getName());
    }

    @Test
    void findAllByRequest_IdTest() {
        List<Item> items = itemRepository.findAllByRequest_Id(itemRequest.getId());

        assertEquals(item.getId(), items.get(0).getId());
        assertEquals(item.getName(), items.get(0).getName());
    }
}