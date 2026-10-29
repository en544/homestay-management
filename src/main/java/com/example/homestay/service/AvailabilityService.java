package com.example.homestay.service;

import com.example.homestay.domain.BookingOrder;
import com.example.homestay.domain.OrderStatus;
import com.example.homestay.domain.Room;
import com.example.homestay.domain.RoomStatus;
import com.example.homestay.repository.BookingOrderRepository;
import com.example.homestay.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AvailabilityService {

    private static final EnumSet<OrderStatus> RESERVING_STATUSES =
            EnumSet.of(OrderStatus.PENDING, OrderStatus.CONFIRMED, OrderStatus.CHECKED_IN);

    private final RoomRepository roomRepository;
    private final BookingOrderRepository orderRepository;

    public AvailabilityService(RoomRepository roomRepository, BookingOrderRepository orderRepository) {
        this.roomRepository = roomRepository;
        this.orderRepository = orderRepository;
    }

    public int reservedRooms(Room room, LocalDate checkIn, LocalDate checkOut) {
        return orderRepository.sumReservedRooms(room.getId(), checkIn, checkOut, RESERVING_STATUSES);
    }

    public int availableRooms(Room room, LocalDate checkIn, LocalDate checkOut) {
        if (room.getStatus() != RoomStatus.AVAILABLE) {
            return 0;
        }
        return Math.max(0, room.getTotalRooms() - reservedRooms(room, checkIn, checkOut));
    }

    public List<Room> searchAvailable(LocalDate checkIn, LocalDate checkOut, Integer guests, String city) {
        return roomRepository.findBookableByStatus(RoomStatus.AVAILABLE).stream()
                .filter(room -> guests == null || room.getCapacity() >= guests)
                .filter(room -> city == null || city.isBlank()
                        || room.getHomestay().getCity().contains(city.trim()))
                .filter(room -> availableRooms(room, checkIn, checkOut) > 0)
                .toList();
    }

    public List<BookingOrder> occupiedOn(LocalDate date) {
        return orderRepository.findOccupiedOn(date, RESERVING_STATUSES);
    }
}
