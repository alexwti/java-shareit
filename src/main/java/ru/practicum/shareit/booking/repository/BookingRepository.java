package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(long bookerId, Sort sort);

    List<Booking> findAllByBookerIdAndStatus(long bookerId, BookingStatus bookingStatus, Sort sort);

    List<Booking> findAllByBookerIdAndStartAfter(long bookerId, LocalDateTime dateTime, Sort sort);

    List<Booking> findAllByBookerIdAndEndBefore(long bookerId, LocalDateTime dateTime, Sort sort);

    @Query("select b from Booking b where b.item.ownerId = ?1 and b.start < ?2 and b.end > ?2")
    List<Booking> findAllByBookerIdCurrent(long bookerId, LocalDateTime dateTimeStart, Sort sort);

    @Query("select b from Booking b where b.item.ownerId = ?1")
    List<Booking> findAllByOwnerId(long ownerId, Sort sort);

    @Query("select b from Booking b where b.item.ownerId = ?1 and b.status = ?2")
    List<Booking> findAllByOwnerIdAndStatus(long ownerId, BookingStatus bookingStatus, Sort sort);

    @Query("select b from Booking b where b.item.ownerId = ?1 and b.start > ?2")
    List<Booking> findAllByOwnerIdAndStartAfter(long ownerId, LocalDateTime dateTime, Sort sort);

    @Query("select b from Booking b where b.item.ownerId = ?1 and b.end < ?2")
    List<Booking> findAllByOwnerIdAndEndBefore(long ownerId, LocalDateTime dateTime, Sort sort);

    @Query("select b from Booking b where b.item.ownerId = ?1 and b.start < ?2 and b.end > ?2")
    List<Booking> findAllByOwnerIdCurrent(long ownerId, LocalDateTime dateTimeStart, Sort sort);

}
