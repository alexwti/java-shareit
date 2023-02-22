package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusIsOrderByStartDesc(long bookerId, BookingStatus bookingStatus, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(long bookerId, LocalDateTime dateTime, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(long bookerId, LocalDateTime dateTime, Pageable pageable);

    @Query("select b from Booking b where b.booker.id = ?1 and b.start < ?2 and b.end > ?2 order by b.start DESC")
    List<Booking> findAllByBookerIdCurrent(long bookerId, LocalDateTime dateTimeStart, Pageable pageable);

    @Query("select b from Booking b where b.item.ownerId = ?1 order by b.start DESC")
    List<Booking> findAllByOwnerId(long ownerId, Pageable pageable);

    @Query("select b from Booking b where b.item.ownerId = ?1 and b.status = ?2 order by b.start DESC")
    List<Booking> findAllByOwnerIdAndStatus(long ownerId, BookingStatus bookingStatus, Pageable pageable);

    @Query("select b from Booking b where b.item.ownerId = ?1 and b.start > ?2 order by b.start DESC")
    List<Booking> findAllByOwnerIdAndStartAfter(long ownerId, LocalDateTime dateTime, Pageable pageable);

    @Query("select b from Booking b where b.item.ownerId = ?1 and b.end < ?2 order by b.start DESC")
    List<Booking> findAllByOwnerIdAndEndBefore(long ownerId, LocalDateTime dateTime, Pageable pageable);

    @Query("select b from Booking b where b.item.ownerId = ?1 and b.start < ?2 and b.end > ?2 order by b.start DESC")
    List<Booking> findAllByOwnerIdCurrent(long ownerId, LocalDateTime dateTimeStart, Pageable pageable);

    @Query("select distinct booking from Booking booking where booking.end < ?2 and booking.item.id = ?1 " +
            "order by booking.start desc ")
    Optional<Booking> findLastBooking(long itemId, LocalDateTime now);

    @Query("select distinct booking from Booking booking where booking.start > ?2 and booking.item.id = ?1 " +
            "order by booking.start ")
    Optional<Booking> findNextBooking(long itemId, LocalDateTime now);

    Optional<Booking> findByBookerIdAndItemIdAndEndBefore(long bookerId, long itemId, LocalDateTime end);
}
