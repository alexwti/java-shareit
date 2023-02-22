package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CommentRepositoryTest {

    User user1;
    User user2;
    @Autowired
    private TestEntityManager em;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private Item item;
    private Comment comment;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();

        user1 = new User(null, "User1 name", "user1@yandex.ru");
        user2 = new User(null, "User2 name", "user2@yandex.ru");
        em.persist(user1);
        em.persist(user2);

        item = Item.builder()
                .id(null)
                .name("Item name")
                .description("Item description")
                .available(true)
                .ownerId(user1.getId())
                .request(null)
                .build();
        em.persist(item);

        comment = Comment.builder()
                .id(null)
                .text("Comment text")
                .item(item)
                .author(user2)
                .created(now)
                .build();
        em.persist(comment);
    }

    @AfterEach
    void afterEach() {
        commentRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllComments() {
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());

        assertEquals(1, comments.size());
        assertEquals(comment.getId(), comments.get(0).getId());
        assertEquals(comment.getAuthor(), comments.get(0).getAuthor());
        assertEquals(comment.getText(), comments.get(0).getText());
    }
}