package com.example.homestay.repository;

import com.example.homestay.domain.Room;
import com.example.homestay.domain.RoomStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @EntityGraph(attributePaths = "homestay")
    List<Room> findAllByOrderByPriceAsc();

    @EntityGraph(attributePaths = "homestay")
    List<Room> findByHomestayIdOrderByPriceAsc(Long homestayId);

    @EntityGraph(attributePaths = "homestay")
    Optional<Room> findWithHomestayById(Long id);

    @EntityGraph(attributePaths = "homestay")
    @Query("select r from Room r where r.status = :status and r.homestay.active = true order by r.price asc")
    List<Room> findBookableByStatus(@Param("status") RoomStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Room r join fetch r.homestay where r.id = :id")
    Optional<Room> findByIdForUpdate(@Param("id") Long id);

    long countByStatus(RoomStatus status);

    @Query("select coalesce(sum(r.totalRooms), 0) from Room r where r.homestay.id = :homestayId")
    long sumTotalRoomsByHomestayId(@Param("homestayId") Long homestayId);
}
