package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository bookingRepository;

    private User user1;
    private User user2;
    private Item item;
    private Booking booking;

    @BeforeEach
    void beforeEach() {
        user1 = new User(null, "User1 name", "user1@yandex.ru");
        em.persist(user1);
        user2 = new User(null, "User2 name", "user2@yandex.ru");
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

        booking = Booking.builder()
                .id(null)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .item(item)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .build();
        em.persist(booking);
    }


    @Test
    void contextLoads() {
        assertNotNull(em);
    }


    @Test
    void findAllByBookerIdOrderByStartDescTest() {
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findAllByBookerIdOrderByStartDesc(user2.getId(), pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findAllByBookerIdAndStatusTest() {
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findAllByBookerIdAndStatusIsOrderByStartDesc(user2.getId(), BookingStatus.WAITING, pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findAllByBookerIdAndStartIsAfterTest() {
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(user2.getId(), LocalDateTime.now().minusHours(4), pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findAllByBookerIdAndEndIsBeforeTest() {
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(user2.getId(), LocalDateTime.now().plusDays(10), pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findAllByBookerIdCurrentTest() {
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findAllByBookerIdCurrent(user2.getId(), LocalDateTime.now().plusDays(2), pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findAllByOwnerIdTest() {
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findAllByOwnerId(user1.getId(), pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));//id могут сбиться, проверить при запуске всех тестов
    }

    @Test
    void findAllByOwnerIdAndStatusTest() {
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findAllByOwnerIdAndStatus(user1.getId(), BookingStatus.WAITING, pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findAllByOwnerIdAndStartAfterTest() {
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findAllByOwnerIdAndStartAfter(user1.getId(), LocalDateTime.now().minusHours(4), pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findAllByOwnerIdAndEndBeforeTest() {
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findAllByOwnerIdAndEndBefore(user1.getId(), LocalDateTime.now().plusDays(10), pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findAllByOwnerIdCurrentTest() {
        PageRequest pg = PageRequest.of(0, 10);

        List<Booking> bookingList = bookingRepository.findAllByOwnerIdCurrent(user1.getId(), LocalDateTime.now().plusDays(2), pg);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findBookingsLastTest() {
        Booking booking1 = booking;
        booking1.setEnd(LocalDateTime.now().plusHours(5));
        booking1.setStart(LocalDateTime.now().plusHours(7));
        em.persist(booking1);

        Optional<Booking> res = bookingRepository.findLastBooking(item.getId(), LocalDateTime.now().plusHours(6));

        assertEquals(booking1, res.get());
    }

    @Test
    void findBookingsNextTest() {
        Booking booking1 = new Booking(
                null,
                item,
                user1,
                BookingStatus.WAITING,
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(6));
        em.persist(booking1);

        Optional<Booking> res = bookingRepository.findNextBooking(item.getId(), LocalDateTime.now().plusDays(4));

        assertEquals(booking1, res.get());
    }

    @Test
    void findByBookerIdAndItemIdAndEndBeforeTest() {
        Optional<Booking> res = bookingRepository.findByBookerIdAndItemIdAndEndBefore(user2.getId(), item.getId(),
                LocalDateTime.now().plusDays(10));
        assertEquals(booking, res.get());
    }
}