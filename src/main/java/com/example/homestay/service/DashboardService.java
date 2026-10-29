package com.example.homestay.service;

import com.example.homestay.domain.BookingOrder;
import com.example.homestay.domain.OrderStatus;
import com.example.homestay.domain.Room;
import com.example.homestay.repository.BookingOrderRepository;
import com.example.homestay.repository.HomestayRepository;
import com.example.homestay.repository.ReviewRepository;
import com.example.homestay.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private static final EnumSet<OrderStatus> OCCUPYING_STATUSES =
            EnumSet.of(OrderStatus.CONFIRMED, OrderStatus.CHECKED_IN);

    private final HomestayRepository homestayRepository;
    private final RoomRepository roomRepository;
    private final BookingOrderRepository orderRepository;
    private final ReviewRepository reviewRepository;

    public DashboardService(
            HomestayRepository homestayRepository,
            RoomRepository roomRepository,
            BookingOrderRepository orderRepository,
            ReviewRepository reviewRepository) {
        this.homestayRepository = homestayRepository;
        this.roomRepository = roomRepository;
        this.orderRepository = orderRepository;
        this.reviewRepository = reviewRepository;
    }

    public DashboardSnapshot snapshot() {
        LocalDate today = LocalDate.now();
        List<Room> rooms = roomRepository.findAllByOrderByPriceAsc();
        long totalInventory = rooms.stream().mapToLong(Room::getTotalRooms).sum();
        int occupied = orderRepository.findOccupiedOn(today, OCCUPYING_STATUSES).stream()
                .mapToInt(BookingOrder::getRoomCount)
                .sum();
        int occupancyRate = totalInventory == 0 ? 0 : (int) Math.round(occupied * 100.0 / totalInventory);

        List<BookingOrder> arrivals = orderRepository.findArrivals(
                today, EnumSet.of(OrderStatus.CONFIRMED, OrderStatus.CHECKED_IN));
        BigDecimal revenue = orderRepository.sumAmountByStatus(OrderStatus.COMPLETED);

        return new DashboardSnapshot(
                homestayRepository.findAllByActiveTrueOrderByCreatedAtDesc().size(),
                totalInventory,
                occupancyRate,
                occupied,
                arrivals.size(),
                orderRepository.countByStatus(OrderStatus.PENDING),
                revenue,
                Math.round(reviewRepository.averageVisibleRating() * 10.0) / 10.0,
                arrivals,
                orderRepository.findTop6ByOrderByCreatedAtDesc(),
                rooms
        );
    }

    public List<OccupancyPoint> weeklyOccupancy() {
        LocalDate today = LocalDate.now();
        long totalInventory = roomRepository.findAllByOrderByPriceAsc().stream()
                .mapToLong(Room::getTotalRooms)
                .sum();
        List<OccupancyPoint> points = new ArrayList<>();
        for (int offset = 0; offset < 7; offset++) {
            LocalDate date = today.plusDays(offset);
            int occupied = orderRepository.findOccupiedOn(date, OCCUPYING_STATUSES).stream()
                    .mapToInt(BookingOrder::getRoomCount)
                    .sum();
            int rate = totalInventory == 0 ? 0 : (int) Math.round(occupied * 100.0 / totalInventory);
            points.add(new OccupancyPoint(date, rate, occupied));
        }
        return points;
    }

    public record DashboardSnapshot(
            long activeHomestays,
            long totalInventory,
            int occupancyRate,
            int occupied,
            long arrivalsToday,
            long pendingOrders,
            BigDecimal revenue,
            double averageRating,
            List<BookingOrder> arrivals,
            List<BookingOrder> recentOrders,
            List<Room> rooms) {
    }

    public record OccupancyPoint(LocalDate date, int rate, int occupied) {
    }
}
