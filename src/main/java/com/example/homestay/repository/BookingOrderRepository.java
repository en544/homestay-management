package com.example.homestay.repository;

import com.example.homestay.domain.BookingOrder;
import com.example.homestay.domain.OrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingOrderRepository extends JpaRepository<BookingOrder, Long> {

    @EntityGraph(attributePaths = {"room", "room.homestay"})
    List<BookingOrder> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"room", "room.homestay"})
    List<BookingOrder> findByStatusOrderByCreatedAtDesc(OrderStatus status);

    @EntityGraph(attributePaths = {"room", "room.homestay"})
    Optional<BookingOrder> findWithRoomById(Long id);

    @EntityGraph(attributePaths = {"room", "room.homestay"})
    List<BookingOrder> findTop6ByOrderByCreatedAtDesc();

    @Query("""
            select coalesce(sum(o.roomCount), 0)
            from BookingOrder o
            where o.room.id = :roomId
              and o.status in :statuses
              and o.checkIn < :checkOut
              and o.checkOut > :checkIn
            """)
    int sumReservedRooms(
            @Param("roomId") Long roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("statuses") Collection<OrderStatus> statuses);

    long countByStatus(OrderStatus status);

    @Query("select coalesce(sum(o.totalAmount), 0) from BookingOrder o where o.status = :status")
    BigDecimal sumAmountByStatus(@Param("status") OrderStatus status);

    @EntityGraph(attributePaths = {"room", "room.homestay"})
    @Query("""
            select o from BookingOrder o
            where o.checkIn = :date
              and o.status in :statuses
            order by o.createdAt desc
            """)
    List<BookingOrder> findArrivals(
            @Param("date") LocalDate date,
            @Param("statuses") Collection<OrderStatus> statuses);

    @Query("""
            select o from BookingOrder o
            where o.checkIn <= :date and o.checkOut > :date
              and o.status in :statuses
            """)
    List<BookingOrder> findOccupiedOn(
            @Param("date") LocalDate date,
            @Param("statuses") Collection<OrderStatus> statuses);
}
